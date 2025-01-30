package alliance.team.stage.controller;

import alliance.team.stage.dto.AuthenticationDto;
import alliance.team.stage.entity.*;
import alliance.team.stage.repository.ConnexionRepository;
import alliance.team.stage.repository.UtilisateurRepository;
import alliance.team.stage.service.*;
import alliance.team.stage.token.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UtilisateurController {
    private final RoleUtilisateurService roleUtilisateurService;
    private final ValidationService validationService;
    private final NotificationService notificationService;
    private final ConnexionRepository connexionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private JWTUtil jwtUtil;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        try {
            List<RoleUtilisateur> roleUtilisateur = utilisateur.getRoles();
            for (RoleUtilisateur role : roleUtilisateur) {
                try {
                    RoleUtilisateur existingRole = roleUtilisateurService.findById(role.getId());
                    if (existingRole == null) {
                        roleUtilisateurService.persisteRoleUtilisateur(role);
                    }
                } catch (NoSuchElementException e) {
                    // Si le rôle n'existe pas, persistez-le.
                    roleUtilisateurService.persisteRoleUtilisateur(role);
                }
            }

            utilisateurService.inscription(utilisateur);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/uploadProfil")
    public ResponseEntity<String> uploadProfil(@RequestParam("file") MultipartFile file,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String token = authorizationHeader.substring(7);
            String userMail = jwtUtil.extractEmailFromToken(token);

            Utilisateur user = utilisateurService.findUserByMail(userMail);

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = "annonce_" + UUID.randomUUID().toString().replace("-", "") + extension;
            user.setMediaPath("/uploads/" + newFileName);
            user.setMediaType(file.getContentType());

            utilisateurRepository.save(user);

            File directory = new File(System.getProperty("user.dir") + "/uploads/profil");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/profil", newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("Image uploadée avec succès : /uploads/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'upload de l'image.");
        }
    }

    @GetMapping(path = "/nbrUtilisateur")
    public ResponseEntity<Long> nbrUtilisateur(){
        return new ResponseEntity<>(utilisateurService.nbrUtilisateur(), HttpStatus.OK);
    }

    @PostMapping("activation")
    private ResponseEntity<String> activation(@RequestBody Activate activate) {
        validationService.activationCompte(activate.getCode(),activate.getMail(),activate.getPassword(),activate.getConfirmPassword() );
        return ResponseEntity.ok("Activation OK");
    }

    @PostMapping("connexion")
    public Object connexion(@RequestBody AuthenticationDto authenticationDto,@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token=authorizationHeader.substring(7);
        String userMail= jwtUtil.extractEmailFromToken(token);

        try {
            log.info("Tentative de connexion pour : {}", authenticationDto.email());

            // Vérifier si l'utilisateur existe
            Utilisateur user = utilisateurService.findUtilisateurByEmail(authenticationDto.email());
            log.info("Utilisateur trouvé : {}", user != null ? user.getEmail() : "Aucun utilisateur trouvé");

            // Authentification via AuthenticationManager
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password())
            );
            log.info("Utilisateur authentifié : {}", authenticate.isAuthenticated());

            Connexion connexion=new Connexion();
            connexion.setDernierConnexion(LocalDateTime.now());
            user.setActive(true);
            utilisateurRepository.save(user);
            connexion.setUtilisateur(user);
            connexionRepository.save(connexion);
            // Générer un token JWT si authentification réussie
            return jwtUtil.generateToken(user);

        } catch (Exception e) {
            log.error("Échec de l'authentification : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erreur", "Identifiants invalides"));
        }
    }

    @PostMapping(path = "passwordForgeted/{email}")
    public ResponseEntity<String> passwordForgeted(@PathVariable String email) {
        try {
            Utilisateur utilisateur = utilisateurService.findUserByMail(email);
            if (utilisateur == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            notificationService.sendNotificationForPassword(email);
        }catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @PutMapping("initialisePassword")
    public ResponseEntity<String> initialisePassword(@RequestBody Code code) {
        try {
            return utilisateurService.initialisePassword(code);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("userList")
    public ResponseEntity<List<Utilisateur>> userList() {
        List<Utilisateur> utilisateurs = utilisateurService.userList();
        for (Utilisateur utilisateur:utilisateurs){
            List<Connexion> selectedConnexions=connexionRepository.findByUtilisateurId(utilisateur.getId());
            if(selectedConnexions !=null){
                utilisateur.setActive(true);
                utilisateurRepository.save(utilisateur);
            }
        }
        if (utilisateurs.isEmpty()) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(utilisateurs);
    }

    @DeleteMapping(path = "deleteUser/{mail}")
    public ResponseEntity<String> deleteUser(@PathVariable String mail) {
        try {
            Utilisateur ut = utilisateurService.findUserByMail(mail);
            utilisateurService.deleteUser(ut);
            return ResponseEntity.ok("Suppression reussie !");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("updateUser")
    public ResponseEntity<String> updateUser(@RequestBody Utilisateur utilisateur) {
        try {
        Utilisateur userToUpdate = utilisateurService.findById(utilisateur.getId());
        userToUpdate.setNom(utilisateur.getNom());
        userToUpdate.setPrenom(utilisateur.getPrenom());
        userToUpdate.setEmail(utilisateur.getEmail());
        userToUpdate.setUsername(utilisateur.getUsername());
        userToUpdate.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateurService.save(userToUpdate);
        return ResponseEntity.ok("Modifiaction reussie !");
        }catch (Exception e) {
            throw new RuntimeException("Modifiaction echouee !");
        }
    }
}
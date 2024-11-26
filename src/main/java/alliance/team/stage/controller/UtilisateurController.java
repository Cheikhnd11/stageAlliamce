package alliance.team.stage.controller;

import alliance.team.stage.dto.AuthenticationDto;
import alliance.team.stage.entity.Activate;
import alliance.team.stage.entity.Code;
import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.service.*;
import alliance.team.stage.token.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class UtilisateurController {
    private final RoleUtilisateurService roleUtilisateurService;
    private final ValidationService validationService;
    private final NotificationService notificationService;
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

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

    @PostMapping("activation")
    private ResponseEntity<String> activation(@RequestBody Activate activate) {
        validationService.activationCompte(activate.getCode(),activate.getMail(),activate.getPassword(),activate.getConfirmPassword() );
        return ResponseEntity.ok("Activation OK");
    }

    @PostMapping("connexion")
    public Object connexion(@RequestBody AuthenticationDto authenticationDto) {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password()));
            Utilisateur user = (Utilisateur) utilisateurService.loadUserByUsername(authenticationDto.email());
            return jwtUtil.generateToken(user);
        }catch (Exception e) {
            log.error("Échec de l'authentification : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erreur", "Identifiants invalides"));
        }
    }

    @PostMapping(path = "passwordForgueted/{email}")
    public ResponseEntity<String> passwordForgueted(@PathVariable String email) {
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
        if (utilisateurs.isEmpty()) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(utilisateurs);
    }

    @DeleteMapping(path = "dalateUser/{mail}")
    public ResponseEntity<String> dalateUser(@PathVariable String mail) {
        try {
            Utilisateur ut = utilisateurService.findUserByMail(mail);
            utilisateurService.delateUser(ut);
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
        }
        catch (Exception e) {
            throw new RuntimeException("Modifiaction echouee !");
        }

    }
}
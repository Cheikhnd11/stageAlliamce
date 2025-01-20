package alliance.team.stage.controller;

import alliance.team.stage.dto.AuthenticationDto;
import alliance.team.stage.dto.UtilisateurDto;
import alliance.team.stage.entity.*;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UtilisateurController {
    private final RoleUtilisateurService roleUtilisateurService;
    private final ValidationService validationService;
    private final NotificationService notificationService;
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private JWTUtil jwtUtil;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody UtilisateurDto utilisateurDto) {
        try {

            if (utilisateurDto.getRoleIds() == null || utilisateurDto.getRoleIds().isEmpty()) {
                throw new IllegalArgumentException("La liste des rolesIds ne peut pas être nulle ou vide.");
            }
            List<RoleUtilisateur> roleUtilisateurList = roleUtilisateurService.findAllById(utilisateurDto.getRoleIds());
            for (RoleUtilisateur role : roleUtilisateurList) {
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
            Utilisateur utilisateur=new Utilisateur();
            utilisateur.setPrenom(utilisateurDto.getPrenom());
            utilisateur.setNom(utilisateurDto.getNom());
            utilisateur.setEmail(utilisateurDto.getEmail());
            utilisateur.setUsername(utilisateurDto.getUsername());
            utilisateur.setRoles(roleUtilisateurList);

            utilisateurService.inscription(utilisateur);
        }catch (Exception e) {
            e.printStackTrace();

        }
    }

    @GetMapping(path = "/verify")
    public ResponseEntity<String> verifyUtilisateur(@RequestParam("token") String token) {
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurByVerificationToken(token);
        System.out.println("Token reçu : " + token);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();

            if (!utilisateur.isVerified()) {
                utilisateur.setVerified(true);
                utilisateurService.save(utilisateur);

                return new ResponseEntity<>(
                        "Compte vérifié avec succès ! Vous pouvez maintenant définir un mot de passe en visitant le formulaire de réinitialisation.",
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        "Le compte a déjà été vérifié.",
                        HttpStatus.BAD_REQUEST
                );
            }
        } else {
            return new ResponseEntity<>(
                    "Lien de vérification invalide ou expiré.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }


    @PostMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();

        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurByVerificationToken(token);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();

            // Vérifier si le token est expiré
            if (utilisateur.isTokenExpired()) {
                return new ResponseEntity<>("Token expiré.", HttpStatus.BAD_REQUEST);
            }

            // Vérifier si l'utilisateur est vérifié
            if (!utilisateur.isVerified()) {
                return new ResponseEntity<>("Le compte n'est pas vérifié.", HttpStatus.BAD_REQUEST);
            }

            // Réinitialiser le mot de passe
            utilisateur.setPassword(passwordEncoder.encode(newPassword));
            utilisateurService.save(utilisateur);
            return new ResponseEntity<>("Mot de passe réinitialisé avec succès !", HttpStatus.OK);
        }

        // Si aucun utilisateur n'est trouvé avec le token donné
        return new ResponseEntity<>("Token de réinitialisation invalide.", HttpStatus.BAD_REQUEST);
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
    public Object connexion(@RequestBody AuthenticationDto authenticationDto) {
        try {
            log.info("Tentative de connexion pour : {}", authenticationDto.email());

            // Vérifier si l'utilisateur existe
            Utilisateur user = utilisateurService.findUtilisateurByEmail(authenticationDto.email());

            user.setActive(true);
            utilisateurService.save(user);
            log.info("Utilisateur trouvé : {}", user != null ? user.getEmail() : "Aucun utilisateur trouvé");

            // Authentification via AuthenticationManager
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password())
            );
            log.info("Utilisateur authentifié : {}", authenticate.isAuthenticated());


            // Générer un token JWT si authentification réussie
            return jwtUtil.generateToken(user);

        } catch (Exception e) {
            log.error("Échec de l'authentification : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erreur", "Identifiants invalides"));
        }

    }

    @PostMapping("deactivate")
    public ResponseEntity<String> deactivateUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            Utilisateur user = utilisateurService.findUtilisateurByEmail(email);
            if (user != null) {
                user.setActive(false);
                utilisateurService.save(user);
                return ResponseEntity.ok("Utilisateur désactivé avec succès.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la désactivation.");
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

    @PutMapping(path = "/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@RequestBody Utilisateur utilisateur, @PathVariable Long id) {
        Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(utilisateur, id);
        if (updatedUtilisateur == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUtilisateur, HttpStatus.OK);
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

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {

            utilisateurService.deleteUser(id);
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

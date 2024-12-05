package alliance.team.stage.service;

import alliance.team.stage.entity.Code;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.repository.CodeRepository;
import alliance.team.stage.repository.UtilisateurRepository;
import alliance.team.stage.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private final ValidationRepository validationRepository;
    private final CodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Collection<GrantedAuthority> authorities = utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getLibelle().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                authorities
        );
    }

    public Utilisateur findUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email));
    }


    private  ValidationService validationService;
    private UtilisateurRepository utilisateurRepository;
    public void inscription(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        validationService.saveValidation(utilisateur);

    }

    public Utilisateur findUserByMail(String mail) {
        return utilisateurRepository.findByEmail(mail)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve pour l'email: "+mail));
    }

    public List<Utilisateur> userList() {return utilisateurRepository.findAll();}

    @Transactional
    public void deleteUser(Utilisateur utilisateur) {
        validationRepository.delete((int) utilisateur.getId());
        utilisateurRepository.delete(utilisateur);
    }

    public Utilisateur findById(long id) {return utilisateurRepository.findById(id).get();}

    public void save(Utilisateur userToUpdate) {
        utilisateurRepository.save(userToUpdate);
    }

    public Long nbrUtilisateur(){
        return utilisateurRepository.count();
    }

    public ResponseEntity<String> initialisePassword(Code code) {
        // Récupération des codes associés à l'email
        List<Code> codesInDb = codeRepository.findByEmail(code.getEmail());

        if (codesInDb.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun code associé à cet email :"+code.getEmail());
        }
        for (Code dbCode : codesInDb) {
            // Vérification du libellé
            if (!dbCode.getLibelle().equals(code.getLibelle())) {
                continue; // Passe au code suivant si le libellé ne correspond pas
            }

            // Vérification de  l'expiration du code
            if (dbCode.getDateExpiration().isBefore(Instant.now())) {
                return ResponseEntity.badRequest().body("Code expiré, veuillez demander un nouveau code !");
            }

            // Vérification du mot de passe et de sa confirmation
            if (!code.getPassword().equals(code.getConfirmationPassword())) {
                return ResponseEntity.badRequest().body("Le mot de passe et la confirmation ne correspondent pas !");
            }

            // Mise à jour du mot de passe utilisateur
            Utilisateur utilisateur = utilisateurRepository.findByEmail(code.getEmail()).get();
            if (utilisateur == null) {
                return ResponseEntity.badRequest().body("Utilisateur introuvable pour cet email !");
            }

            utilisateur.setPassword(passwordEncoder.encode(code.getPassword()));
            utilisateurRepository.save(utilisateur); // Assurez-vous d'avoir cette méthode dans votre service
            return ResponseEntity.ok("Mot de passe mis à jour avec succès !");
        }

        return ResponseEntity.badRequest().body("Code invalide !");
    }
}
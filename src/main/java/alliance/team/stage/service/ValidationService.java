package alliance.team.stage.service;

import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.entity.ValidationCompte;
import alliance.team.stage.repository.UtilisateurRepository;
import alliance.team.stage.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Random;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@AllArgsConstructor
@Transactional
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;
    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveValidation(Utilisateur utilisateur) {
        ValidationCompte validationCompte = new ValidationCompte();
        validationCompte.setUtilisateur(utilisateur);
        Instant creationDate = Instant.now();
        validationCompte.setCreationDate(creationDate);
        Instant expirationDate = creationDate.plus(10, MINUTES);
        validationCompte.setExpirationDate(expirationDate);
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(999999));
        validationCompte.setCode(code);
        validationRepository.save(validationCompte);
        notificationService.sendNotification(validationCompte);
    }

    public boolean activationCompte(String code,String mail, String password,String confirmePassword) {
        ValidationCompte validationCompte = validationRepository.findByUtilisateur_Email(mail);
        if (validationCompte == null) {
            throw new IllegalArgumentException("Utilisateur non trouvable");
        }
        if (!validationCompte.getCode().equals(code)) {
            throw new IllegalArgumentException("Code invalid");
        }
        if (validationCompte.getExpirationDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration date invalid");
        }
        if (!password.equals(confirmePassword)) {
            throw new IllegalArgumentException("Password incorrect");
        }
        validationCompte.setActivationDate(Instant.now());
        validationRepository.save(validationCompte);
        Utilisateur utilisateur = validationCompte.getUtilisateur();
        utilisateur.setPassword(bCryptPasswordEncoder.encode(password));
        utilisateur.setActive(true);
        utilisateurRepository.save(utilisateur);
        return true;
    }
}
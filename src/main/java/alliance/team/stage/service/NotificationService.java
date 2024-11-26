package alliance.team.stage.service;

import alliance.team.stage.entity.Code;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.entity.ValidationCompte;
import alliance.team.stage.repository.CodeRepository;
import alliance.team.stage.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@AllArgsConstructor

public class NotificationService {
    private final JavaMailSender javaMailSender;
    private final UtilisateurRepository utilisateurRepository;
    private final CodeRepository codeRepository;

    public void sendNotification(ValidationCompte validationCompte) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cn7061611@gmail.com");
        message.setTo(validationCompte.getUtilisateur().getEmail());
        message.setSubject("code de validation");
        String texte = String.format("Bonjour "+validationCompte.getUtilisateur().getPrenom()+" "+validationCompte.getUtilisateur().getNom()+
                " , voici votre code d'activation: "+validationCompte.getCode()+" a biento");
        message.setText(texte);
        javaMailSender.send(message);
    }

    public void sendNotificationForPassword(String email){
        Code codePasswor = new Code();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur n'existe pas"));
        Random rand = new Random();
        String code = String.format("%06d", rand.nextInt(999999));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cn7061611@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("code de modification password");
        String texte = String.format("Bonjour "+user.getPrenom()+" "+user.getNom()+",/n Voici votre code pour modifier votre mot de passe: /n"+code+" valable pour 10 minutes.");
        message.setText(texte);
        codePasswor.setLibelle(code);
        codePasswor.setDateCreation(Instant.now());
        codePasswor.setDateExpiration(Instant.now().plus(10, MINUTES));
        codePasswor.setEmail(email);
        javaMailSender.send(message);
        codeRepository.save(codePasswor);
    }
}

package alliance.team.stage.service;

import alliance.team.stage.entity.ValidationCompte;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public void sendNotification(ValidationCompte validationCompte) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cn7061611@gmail.com");
        message.setTo(validationCompte.getUtilisateur().getEmail());
        message.setSubject("code de validation");
        String texte = String.format("Bonjour " + validationCompte.getUtilisateur().getPrenom() + " " + validationCompte.getUtilisateur().getNom() + " , voici votre code d'activation: " + validationCompte.getCode() + " a biento");
        message.setText(texte);
        javaMailSender.send(message);
    }
}
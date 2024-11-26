package alliance.team.stage.service;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.entity.Notification;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.entity.ValidationCompte;
import alliance.team.stage.repository.NotificationRepository;
import alliance.team.stage.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final JavaMailSender javaMailSender;
    private NotificationRepository notificationRepository;
    private UtilisateurRepository utilisateurRepository;

    public void sendMail(ValidationCompte validationCompte) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cn7061611@gmail.com");
        message.setTo(validationCompte.getUtilisateur().getEmail());
        message.setSubject("code de validation");
        String texte = String.format("Bonjour " + validationCompte.getUtilisateur().getPrenom() + " " + validationCompte.getUtilisateur().getNom() + " , voici votre code d'activation: " + validationCompte.getCode() + " a biento");
        message.setText(texte);
        javaMailSender.send(message);
    }

    public void sendNotification(Annonce annonce) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        for (Utilisateur utilisateur : utilisateurs) {
            Notification notification = new Notification(annonce, utilisateur);
            notificationRepository.save(notification);
        }
    }
}
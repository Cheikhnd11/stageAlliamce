package alliance.team.stage.repository;

import alliance.team.stage.entity.Notification;
import alliance.team.stage.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateur(Utilisateur utilisateur);
}
package alliance.team.stage.repository;

import alliance.team.stage.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
<<<<<<< HEAD

    Optional<Utilisateur> findByEmail(String email);
=======
    Utilisateur findByLogin(String login);
>>>>>>> 3a9ae2f6bc4a2fa02533f78556a9a018caad606a
}

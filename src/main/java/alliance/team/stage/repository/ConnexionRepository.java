package alliance.team.stage.repository;

import alliance.team.stage.entity.Connexion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnexionRepository extends JpaRepository<Connexion,Long> {
    List<Connexion> findByUtilisateurId(Long id);
}
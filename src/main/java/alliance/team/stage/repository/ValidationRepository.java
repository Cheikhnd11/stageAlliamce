package alliance.team.stage.repository;

import alliance.team.stage.entity.ValidationCompte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationRepository extends JpaRepository<ValidationCompte, Integer> {
    ValidationCompte findByUtilisateur_Email(String email);
}
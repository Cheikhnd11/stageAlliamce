package alliance.team.stage.repository;

import alliance.team.stage.entity.ValidationCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ValidationRepository extends JpaRepository<ValidationCompte, Integer> {
    ValidationCompte findByUtilisateur_Email(String email);

    @Modifying
    @Query("DELETE FROM ValidationCompte v WHERE v.utilisateur.id = :utilisateurId")
    void delete(@Param("utilisateurId") int utilisateurId);
}

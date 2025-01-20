package alliance.team.stage.repository;

import alliance.team.stage.entity.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleUtilisateurRepository extends JpaRepository<RoleUtilisateur, Integer> {
    List<RoleUtilisateur> findAllById(Iterable<Integer> ids);
}

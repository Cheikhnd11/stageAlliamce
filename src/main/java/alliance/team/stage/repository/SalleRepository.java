package alliance.team.stage.repository;

import alliance.team.stage.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    Optional<Salle> findByName(String name);
}
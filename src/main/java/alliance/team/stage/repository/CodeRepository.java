package alliance.team.stage.repository;

import alliance.team.stage.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findByEmail(String email);
}

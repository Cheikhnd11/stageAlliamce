package alliance.team.stage.service;

import alliance.team.stage.entity.Salle;
import alliance.team.stage.repository.SalleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SalleService {
    private SalleRepository salleRepository;

    public void save(Salle salle) {
        salleRepository.save(salle);
    }

    public Optional<Salle> findById(Long id) {
        return salleRepository.findById(id);
    }

    public List<Salle> findAll() {
        return salleRepository.findAll();
    }

    public void delete(Long id) {
        salleRepository.deleteById(id);
    }

    public Boolean findByName(String name) {
        return salleRepository.findByName(name).isPresent();
    }
}
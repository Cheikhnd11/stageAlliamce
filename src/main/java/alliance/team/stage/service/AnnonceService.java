package alliance.team.stage.service;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.repository.AnnonceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnonceService {
    private AnnonceRepository annonceRepository;

    public void addAnnonce(Annonce annonce) {
        annonceRepository.save(annonce);
    }

    public void deleteAnnonce(Annonce annonce) {
        annonceRepository.delete(annonce);
    }
}
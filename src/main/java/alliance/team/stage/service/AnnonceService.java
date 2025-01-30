package alliance.team.stage.service;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.repository.AnnonceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Service
@AllArgsConstructor
@Transactional
public class AnnonceService {
    private AnnonceRepository annonceRepository;

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public Optional<Annonce> getAnnonceById(Long id) {
        return annonceRepository.findById(id);
    }

    public void deleteAnnonce(Long id) {
        annonceRepository.deleteById(id);
    }

    public void addAnnonce(Annonce annonce) {
        annonceRepository.save(annonce);
    }

    public String saveMedia(MultipartFile file) throws Exception {
        String baseUploadDir = "C:" + File.separator + "uploads";

        // Création du sous-dossier si nécessaire
        File directory = new File(baseUploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Enregistrement du fichier
        String filePath = baseUploadDir + File.separator + file.getOriginalFilename();
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        return filePath; // Retourne le chemin complet du fichier
    }
}
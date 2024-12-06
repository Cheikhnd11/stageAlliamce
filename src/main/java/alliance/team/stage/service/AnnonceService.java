package alliance.team.stage.service;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.entity.Notification;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.repository.AnnonceRepository;
import alliance.team.stage.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Service
@AllArgsConstructor
public class AnnonceService {
    private AnnonceRepository annonceRepository;
    private NotificationRepository notificationRepository;
    private UtilisateurService utilisateurService;

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

    public List<Annonce> getAllAnnoncesForUser(Long userid) {
        Utilisateur user = utilisateurService.findById(userid);
        List<Notification> notifications = notificationRepository.findByUtilisateur(user);
        List<Annonce> annonces = new java.util.ArrayList<>();
        for (Notification notification : notifications) {
            annonces.add(notification.getAnnonce());
        }
        return annonces;
    }
}
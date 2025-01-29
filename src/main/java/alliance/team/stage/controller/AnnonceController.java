package alliance.team.stage.controller;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.service.AnnonceService;
import alliance.team.stage.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("annonces")
public class AnnonceController {
    private final AnnonceService annonceService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        return annonceService.getAnnonceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("ajoutAnnonce")
    public ResponseEntity<String> uploadImage(@RequestParam("titre") String titre,
                                              @RequestParam("description") String description,
                                              @RequestParam("file") MultipartFile file) {
        try {
            // Générer un nom unique pour le fichier (UUID + extension)
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = "annonce_" + UUID.randomUUID().toString().replace("-", "") + extension;

            // Créer l'objet Annonce
            Annonce annonce = new Annonce();
            annonce.setTitre(titre);
            annonce.setDescription(description);
            annonce.setDatePublication(LocalDateTime.now());
            annonce.setMediaPath("/uploads/" + newFileName);
            annonce.setMediaType(file.getContentType());

            annonceService.addAnnonce(annonce);

            // Vérifier si le dossier existe, sinon le créer
            File directory = new File(System.getProperty("user.dir") + "/uploads");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Sauvegarde du fichier avec le nom personnalisé
            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads", newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            notificationService.sendAnnonce(annonce);

            return ResponseEntity.ok("Image uploadée avec succès : /uploads/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'upload de l'image.");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAnnonce(@PathVariable Long id) {
        try {
            annonceService.deleteAnnonce(id);
            return ResponseEntity.ok("Annonce supprimée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression de l'annonce : " + e.getMessage());
        }
    }
}
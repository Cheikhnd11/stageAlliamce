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
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("annonces")
public class AnnonceController {
    private AnnonceService annonceService;
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("actualite")
    public ResponseEntity<List<Annonce>> getAnnoncesByUser(@RequestParam Long userid) {
        List<Annonce> annonces = annonceService.getAllAnnoncesForUser(userid);
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        return annonceService.getAnnonceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("ajoutAnnonce")
    public ResponseEntity<String> addAnnonce(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            Annonce annonce = new Annonce();
            annonce.setTitre(titre);
            annonce.setDescription(description);

            // Gestion du fichier
            if (file != null && !file.isEmpty()) {
                String filePath = annonceService.saveMedia(file);
                annonce.setMediaPath(filePath);
                annonce.setMediaType(file.getContentType());
            }

            // Sauvegarde de l'annonce
            annonceService.addAnnonce(annonce);
            notificationService.sendAnnonce(annonce);
            return ResponseEntity.ok("Annonce ajoutée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout de l'annonce : " + e.getMessage());
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
package alliance.team.stage.controller;

import alliance.team.stage.entity.Salle;
import alliance.team.stage.service.SalleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("salle")
public class SalleController {
    private final SalleService salleService;

    @GetMapping
    public ResponseEntity<List<Salle>> getAllSalles() {
        List<Salle> salles = salleService.findAll();
        return ResponseEntity.ok(salles);
    }

    @GetMapping("{id}")
    public ResponseEntity<Salle> getSalleById(@PathVariable Long id) {
        return salleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSalle(@PathVariable Long id) {
        try {
            salleService.delete(id);
            return ResponseEntity.ok("Salle supprimée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression de la salle : " + e.getMessage());
        }
    }

    @PostMapping("ajoutSalle")
    public ResponseEntity<String> ajoutSalle(@RequestBody Salle salle) {
        try {
            salleService.save(salle);
            return ResponseEntity.ok("Salle ajoutée avec succés");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout de la salle : " + e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateSalle(@PathVariable Long id, @RequestBody Salle salle) {
        try {
            Salle s = salleService.findById(id).get();
            s.setName(salle.getName());
            s.setNbPlace(salle.getNbPlace());
            salleService.save(s);
            return ResponseEntity.ok("Salle modifiée avec succés");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de le modification : " + e.getMessage());
        }
    }
}
package alliance.team.stage.controller;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.repository.RoleUtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/roles")
@AllArgsConstructor
public class RoleController {
    private final RoleUtilisateurRepository roleUtilisateurRepository;

    @GetMapping
    public ResponseEntity<List<RoleUtilisateur>> getRoles() {
        return ResponseEntity.ok(roleUtilisateurRepository.findAll());
    }
}
package alliance.team.stage.controller;

import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/utilisateur")
public class UtilisateurController {
    private UtilisateurService utilisateurService;

    @GetMapping(path = "{login}")
    public Utilisateur utilisateur(@PathVariable String login) {
        return utilisateurService.getUtilisateurByLogin(login);
    }

    @PostMapping
    public void ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {utilisateurService.addUtilisateur(utilisateur);}
}
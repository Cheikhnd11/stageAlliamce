package alliance.team.stage.controller;

import alliance.team.stage.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UtilisateurController {
    private UtilisateurService utilisateurService;
}

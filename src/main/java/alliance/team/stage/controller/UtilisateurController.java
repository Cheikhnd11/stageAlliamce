package alliance.team.stage.controller;

import alliance.team.stage.dto.AuthenticationDto;
import alliance.team.stage.entity.Activate;
import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.enumeration.TypeOfRole;
import alliance.team.stage.service.RoleUtilisateurService;
import alliance.team.stage.service.UtilisateurService;
import alliance.team.stage.service.ValidationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class UtilisateurController {
    private final RoleUtilisateurService roleUtilisateurService;
    private final ValidationService validationService;
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        try {
            RoleUtilisateur roleUtilisateur = new RoleUtilisateur();
            roleUtilisateur.setLibelle(TypeOfRole.USER);
            roleUtilisateurService.persisteRoleUtilisateur(roleUtilisateur);
            List<RoleUtilisateur> role=new ArrayList<>();
            role.add(roleUtilisateur);
            utilisateur.setRoles(role);
            utilisateurService.inscription(utilisateur);
        }catch (Exception e) {
            e.printStackTrace();

        }
    }

    @PostMapping("activation")
    private ResponseEntity<String> activation(@RequestBody Activate activate) {
        validationService.activationCompte(activate.getCode(),activate.getMail(),activate.getPassword());
        return ResponseEntity.ok("Activation OK");
    }

    @PostMapping("connexion")
    public Map<String, String> connexion(@RequestBody AuthenticationDto authenticationDto) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDto.userName(), authenticationDto.password()));
        log.info("Resultat{}",authenticate.isAuthenticated());
        return null;
    }

}

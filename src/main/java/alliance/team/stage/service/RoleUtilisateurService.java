package alliance.team.stage.service;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.repository.RoleUtilisateurRipository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RoleUtilisateurService {
    private final RoleUtilisateurRipository roleUtilisateurRipository;
    public void persisteRoleUtilisateur(RoleUtilisateur role) {roleUtilisateurRipository.save(role);}
}
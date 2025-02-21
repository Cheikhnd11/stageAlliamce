package alliance.team.stage.service;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.repository.RoleUtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
@Transactional
public class RoleUtilisateurService {

    private final RoleUtilisateurRepository roleUtilisateurRipository;

    public void persisteRoleUtilisateur(RoleUtilisateur role) {roleUtilisateurRipository.save(role);}

    public RoleUtilisateur findById(int id) {
        return roleUtilisateurRipository.findById(id).orElseThrow(() -> new NoSuchElementException("Pas d'utilisateur avec l'id: "+id));
    }
}
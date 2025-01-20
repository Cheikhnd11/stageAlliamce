package alliance.team.stage.service;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.repository.RoleUtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class RoleUtilisateurService {

    private final RoleUtilisateurRepository roleUtilisateurRepository;

    public void persisteRoleUtilisateur(RoleUtilisateur role) {roleUtilisateurRepository.save(role);}

    public RoleUtilisateur findById(int id) {return roleUtilisateurRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Pas d'utilisateur avec L'id: "+id));

    }
    public List<RoleUtilisateur> findAllById(Iterable<Integer> ids) {
        return roleUtilisateurRepository.findAllById(ids);
    }
}

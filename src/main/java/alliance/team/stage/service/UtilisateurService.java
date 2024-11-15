package alliance.team.stage.service;

import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UtilisateurService {
    private UtilisateurRepository utilisateurRepository;
    public Utilisateur getUtilisateurByLogin(String login) {
        return utilisateurRepository.findByLogin(login);
    }

    public  void  addUtilisateur(Utilisateur utilisateur) {utilisateurRepository.save(utilisateur);}
}

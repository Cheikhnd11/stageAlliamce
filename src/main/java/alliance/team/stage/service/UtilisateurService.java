package alliance.team.stage.service;

import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private  ValidationService validationService;
    private UtilisateurRepository utilisateurRepository;
    public void inscription(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        validationService.saveValidation(utilisateur);

    }

}
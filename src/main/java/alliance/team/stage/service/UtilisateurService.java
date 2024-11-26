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
    private  ValidationService validationService;
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void inscription(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        validationService.saveValidation(utilisateur);
    }
}
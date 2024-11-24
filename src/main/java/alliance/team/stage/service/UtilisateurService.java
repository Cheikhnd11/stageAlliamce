package alliance.team.stage.service;

import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.repository.UtilisateurRepository;
import alliance.team.stage.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private final ValidationRepository validationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Collection<GrantedAuthority> authorities = utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getLibelle().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                authorities
        );
    }

    private  ValidationService validationService;
    private UtilisateurRepository utilisateurRepository;
    public void inscription(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        validationService.saveValidation(utilisateur);

    }

    public Utilisateur findUserByMail(String mail) {return utilisateurRepository.findByEmail(mail)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve pour l'email: "+mail));
    }

    public List<Utilisateur> userList() {return utilisateurRepository.findAll();}

    @Transactional
    public void delateUser(Utilisateur utilisateur) {
        validationRepository.delete((int) utilisateur.getId());
        utilisateurRepository.delete(utilisateur);}

    public Utilisateur findById(long id) {return utilisateurRepository.findById(id).get();}

    public void save(Utilisateur userToUpdate) {utilisateurRepository.save(userToUpdate);
    }
}
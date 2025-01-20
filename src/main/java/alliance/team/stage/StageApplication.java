package alliance.team.stage;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.enumeration.TypeOfRole;
import alliance.team.stage.service.RoleUtilisateurService;
import alliance.team.stage.service.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class StageApplication implements CommandLineRunner {

	private final BCryptPasswordEncoder passwordEncoder;
	private final UtilisateurService utilisateurService;
	private final RoleUtilisateurService roleUtilisateurService;

	public StageApplication(BCryptPasswordEncoder passwordEncoder, UtilisateurService utilisateurService, RoleUtilisateurService roleUtilisateurService) {
		this.passwordEncoder = passwordEncoder;
		this.utilisateurService = utilisateurService;
		this.roleUtilisateurService = roleUtilisateurService;
	}

	public static void main(String[] args) {
		SpringApplication.run(StageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
//		String password=passwordEncoder.encode("Passer123");
//		Utilisateur utilisateur=new Utilisateur();
//		RoleUtilisateur roleUtilisateur=new RoleUtilisateur();
//		RoleUtilisateur roleUtilisateur1=new RoleUtilisateur();
//		RoleUtilisateur roleUtilisateur2=new RoleUtilisateur();
//
//		utilisateur.setUsername("amza");
//		utilisateur.setPrenom("cheikhna");
//		utilisateur.setNom("ndiaye");
//		utilisateur.setVerified(true);
//		utilisateur.setActive(true);
//		utilisateur.setEmail("admin@gmail.com");
//		roleUtilisateur.setLibelle(TypeOfRole.ROLE_ADMIN);
//		roleUtilisateur1.setLibelle(TypeOfRole.ROLE_GESTIONNAIRE);
//		roleUtilisateur1.setLibelle(TypeOfRole.ROLE_USER);
//		roleUtilisateurService.persisteRoleUtilisateur(roleUtilisateur);
//		List<RoleUtilisateur> roleUtilisateurs=new ArrayList<>();
//		roleUtilisateurs.add(roleUtilisateur);
//		utilisateur.setRoles(roleUtilisateurs);
//		utilisateur.setPassword(password);
//		utilisateurService.save(utilisateur);

	}

}

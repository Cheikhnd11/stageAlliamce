package alliance.team.stage;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import alliance.team.stage.enumeration.TypeOfRole;
import alliance.team.stage.service.RoleUtilisateurService;
import alliance.team.stage.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class StageApplication implements CommandLineRunner {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleUtilisateurService roleUtilisateurService;
    @Autowired
    private UtilisateurService utilisateurService;

	public static void main(String[] args) {
		SpringApplication.run(StageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password = passwordEncoder.encode("Passer123");
		Utilisateur admin = new Utilisateur();
		admin.setNom("admin");
		admin.setPrenom("admin");
		admin.setEmail("smouhamadou06@gmail.com");
		admin.setUsername("admin");
		admin.setPassword(password);
		admin.setActive(true);

		RoleUtilisateur role = new RoleUtilisateur();
		role.setLibelle(TypeOfRole.ROLE_ADMIN);
		roleUtilisateurService.persisteRoleUtilisateur(role);
		List<RoleUtilisateur> roleUtilisateurs = new ArrayList<>();
		roleUtilisateurs.add(role);
		admin.setRoles(roleUtilisateurs);
		utilisateurService.save(admin);
	}
}

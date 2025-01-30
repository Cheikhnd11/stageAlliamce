package alliance.team.stage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDto {
    private String prenom;
    private String nom;
    private String email;
    private String username;
    private String password;
    private List<Integer> roleIds;
}
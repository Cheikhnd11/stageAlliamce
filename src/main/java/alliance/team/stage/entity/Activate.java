package alliance.team.stage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Activate {
    private String code;
    private String mail;
    private String password;
    private String confirmPassword;
}
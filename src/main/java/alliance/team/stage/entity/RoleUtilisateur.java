package alliance.team.stage.entity;

import alliance.team.stage.enumeration.TypeOfRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class RoleUtilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private TypeOfRole libelle;

    public RoleUtilisateur(String libelle) {
        this.libelle = TypeOfRole.valueOf(libelle);
    }
}
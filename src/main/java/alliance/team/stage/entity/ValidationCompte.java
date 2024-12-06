package alliance.team.stage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ValidationCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Instant creationDate;
    private Instant expirationDate;
    private Instant activationDate;
    private String code;
    @OneToOne(cascade = CascadeType.ALL)
    private Utilisateur utilisateur;
}
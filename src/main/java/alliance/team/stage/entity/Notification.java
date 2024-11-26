package alliance.team.stage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "annonce_id")
    private Annonce annonce;
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    private boolean vue = false;

    public Notification(Annonce annonce, Utilisateur utilisateur) {
        this.annonce = annonce;
        this.utilisateur = utilisateur;
    }
}
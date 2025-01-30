package alliance.team.stage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String titre;
    private String description;
    private String mediaPath;
    private String mediaType;
    @Column(nullable = false)
    private LocalDateTime datePublication;

    @PrePersist
    public void prePersist() {
        this.datePublication = LocalDateTime.now();
    }
}
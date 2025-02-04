package alliance.team.stage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reservation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"salle_id", "startDate", "endDate"}))
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Client client;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Salle salle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDate date;
}
package alliance.team.stage.service;

import alliance.team.stage.entity.Client;
import alliance.team.stage.entity.Reservation;
import alliance.team.stage.entity.Salle;
import alliance.team.stage.repository.ClientRepository;
import alliance.team.stage.repository.ReservationRepository;
import alliance.team.stage.repository.SalleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ReservationService {
    private ReservationRepository reservationRepository;
    private SalleRepository salleRepository;
    private ClientRepository clientRepository;

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Boolean reservation(Reservation reservation) {
        Client client = clientRepository.findByEmail(reservation.getClient().getEmail())
                .orElseGet(() -> clientRepository.save(reservation.getClient()));
        reservation.setClient(client);
        Salle salle = salleRepository.findById(reservation.getSalle().getId())
                .orElseGet(() -> salleRepository.save(reservation.getSalle()));
        reservation.setSalle(salle);
        reservation.setDate(LocalDate.now());
        List<Reservation> conflits = reservationRepository.findConflictingReservations(salle.getId(), reservation.getStartDate(), reservation.getEndDate());

        if (!conflits.isEmpty()) {
            return false; // Salle déjà réservée à ces dates
        }

        reservationRepository.save(reservation);
        return true; // Réservation confirmée
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
}
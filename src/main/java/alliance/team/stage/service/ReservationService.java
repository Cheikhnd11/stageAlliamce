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

    public void reservation(Reservation reservation) {
        Client client = clientRepository.findByEmail(reservation.getClient().getEmail())
                .orElseGet(() -> clientRepository.save(reservation.getClient()));
        reservation.setClient(client);
        Salle salle = salleRepository.findById(reservation.getSalle().getId())
                .orElseGet(() -> salleRepository.save(reservation.getSalle()));
        reservation.setSalle(salle);
        save(reservation);
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
}
package alliance.team.stage.controller;

import alliance.team.stage.entity.Client;
import alliance.team.stage.entity.Reservation;
import alliance.team.stage.entity.Salle;
import alliance.team.stage.service.ClientService;
import alliance.team.stage.service.ReservationService;
import alliance.team.stage.service.SalleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final ClientService clientService;
    private final SalleService salleService;

    @PostMapping("ajoutReservation")
    public ResponseEntity<String> ajoutReservation(@RequestBody Reservation reservation) {
        try {
            Client client = reservation.getClient();
            if (!clientService.getClientByEmail(client.getEmail())) {
                clientService.addClient(client);
                reservation.setClient(client);
            }
            Salle salle = reservation.getSalle();
            if (!salleService.findByName(salle.getName())) {
                salleService.save(salle);
                reservation.setSalle(salle);
            }
            reservationService.save(reservation);
            return ResponseEntity.ok("Salle réservée avec succés");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la réservation de la salle : " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.delete(id);
            return ResponseEntity.ok("Réservation supprimée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        try {
            Client client = reservation.getClient();
            if (!(clientService.getClientByEmail(client.getEmail()))) {
                clientService.addClient(client);
                reservation.setClient(client);
            }
            Salle salle = reservation.getSalle();
            if (!(salleService.findByName(salle.getName()))) {
                salleService.save(salle);
                reservation.setSalle(salle);
            }

            Reservation r = reservationService.findById(id).get();
            r.setClient(reservation.getClient());
            r.setSalle(reservation.getSalle());
            r.setDate(reservation.getDate());
            r.setEndDate(reservation.getEndDate());
            r.setStartDate(reservation.getStartDate());
            reservationService.save(r);
            return ResponseEntity.ok("Salle modifiée avec succés");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de le modification : " + e.getMessage());
        }
    }
}
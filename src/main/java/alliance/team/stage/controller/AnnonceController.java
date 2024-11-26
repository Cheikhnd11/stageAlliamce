package alliance.team.stage.controller;

import alliance.team.stage.entity.Annonce;
import alliance.team.stage.service.AnnonceService;
import alliance.team.stage.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class AnnonceController {
    private AnnonceService annonceService;
    private NotificationService notificationService;

    @PostMapping("ajoutAnnonce")
    public void ajouterAnnonce(@RequestBody Annonce annonce) {
        annonceService.addAnnonce(annonce);
        notificationService.sendNotification(annonce);
    }
}
package com.rabbitfarm.service;

import com.rabbitfarm.model.*;
import com.rabbitfarm.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SevrageService {

    private final PorteeRepository porteeRepository;
    private final LotEngraissementRepository lotRepository;
    private final CageRepository cageRepository;
    private final UtilisateurContextService utilisateurContextService;

    public LotEngraissement sevrerPortee(Long porteeId) {
        Portee portee = porteeRepository.findById(porteeId)
                .orElseThrow(() -> new EntityNotFoundException("Portée introuvable : " + porteeId));

        if (portee.isSevree()) {
            throw new IllegalStateException("Cette portée a déjà été sevrée.");
        }

        if (portee.getNbVivants() == null || portee.getNbVivants() <= 0) {
            throw new IllegalStateException("Aucun lapereau vivant à sevrer dans cette portée.");
        }

        return creerLotDepuisPortee(portee, LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void sevrageAutomatique() {
        LocalDate aujourdHui = LocalDate.now();
        List<Portee> aSetablirPortees = porteeRepository.findPorteesASeurer(aujourdHui);

        if (aSetablirPortees.isEmpty()) {
            log.info("Sevrage automatique : aucune portée à sevrer aujourd'hui.");
            return;
        }

        for (Portee portee : aSetablirPortees) {
            try {
                creerLotDepuisPortee(portee, aujourdHui);
                log.info("Sevrage automatique effectué pour portée #{} (lapine: {})",
                        portee.getId(), portee.getLapine().getNom());
            } catch (Exception e) {
                log.error("Erreur sevrage automatique portée #{} : {}", portee.getId(), e.getMessage());
            }
        }
    }

    private LotEngraissement creerLotDepuisPortee(Portee portee, LocalDate dateSevrage) {
        int nbLapereaux = portee.getNbVivants();

        // ✅ Chercher une cage libre appartenant au même utilisateur que la portée
        Utilisateur proprietaire = portee.getUtilisateur();
        Cage cage = cageRepository.findFirstByUtilisateurAndTypeAndStatut(
                proprietaire, Cage.TypeCage.ENGRAISSEMENT, Cage.StatutCage.LIBRE).orElse(null);

        LotEngraissement lot = LotEngraissement.builder()
                .portee(portee)
                .cage(cage)
                .nombreInitial(nbLapereaux)
                .nombreActuel(nbLapereaux)
                .nombreDeces(0)
                .dateEntree(dateSevrage)
                .statut(LotEngraissement.StatutLot.EN_COURS)
                // ✅ Le lot appartient au même utilisateur que la portée
                .utilisateur(proprietaire)
                .notes("Créé automatiquement au sevrage de la portée #" + portee.getId())
                .build();

        lotRepository.save(lot);

        if (cage != null) {
            cage.setStatut(Cage.StatutCage.OCCUPEE);
            cageRepository.save(cage);
        }

        portee.setSevree(true);
        portee.setStatut(Portee.StatutPortee.SEVREE);
        porteeRepository.save(portee);

        log.info("Lot d'engraissement créé : {} lapereaux depuis portée #{}", nbLapereaux, portee.getId());

        return lot;
    }

    public LotEngraissement enregistrerDeces(Long lotId, int nombreDeces) {
        LotEngraissement lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable : " + lotId));

        if (nombreDeces > lot.getNombreActuel()) {
            throw new IllegalArgumentException("Le nombre de décès dépasse le nombre actuel de lapereaux.");
        }

        lot.setNombreActuel(lot.getNombreActuel() - nombreDeces);
        lot.setNombreDeces(lot.getNombreDeces() + nombreDeces);

        if (lot.getNombreActuel() == 0) {
            lot.setStatut(LotEngraissement.StatutLot.TERMINE);
        }

        return lotRepository.save(lot);
    }

    public LotEngraissement cloturerLot(Long lotId, LotEngraissement.StatutLot statut) {
        LotEngraissement lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable : " + lotId));

        lot.setStatut(statut);
        lot.setDateSortie(LocalDate.now());

        if (lot.getCage() != null) {
            lot.getCage().setStatut(Cage.StatutCage.LIBRE);
            cageRepository.save(lot.getCage());
        }

        return lotRepository.save(lot);
    }

    public int getTotalLapereauEnEngraissement() {
        Integer total = lotRepository.sumLapereauEnCours(utilisateurContextService.getUtilisateurConnecte());
        return total != null ? total : 0;
    }

    public List<LotEngraissement> getLotsEnCours() {
        return lotRepository.findByUtilisateurAndStatut(
                utilisateurContextService.getUtilisateurConnecte(), LotEngraissement.StatutLot.EN_COURS);
    }

    public List<LotEngraissement> getTousLesLots() {
        return lotRepository.findByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public LotEngraissement ajouterLapereaux(Long lotId, int nombre, String motif) {
        LotEngraissement lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable : " + lotId));

        if (lot.getStatut() != LotEngraissement.StatutLot.EN_COURS) {
            throw new IllegalStateException("Impossible d'ajouter des lapereaux à un lot clôturé.");
        }

        lot.setNombreActuel(lot.getNombreActuel() + nombre);
        lot.setNombreInitial(lot.getNombreInitial() + nombre);

        String noteAjout = "+" + nombre + " lapereaux ajoutés"
                + (motif != null && !motif.isBlank() ? " (" + motif + ")" : "")
                + " le " + LocalDate.now();
        lot.setNotes(lot.getNotes() != null
                ? lot.getNotes() + " | " + noteAjout
                : noteAjout);

        return lotRepository.save(lot);
    }

    public LotEngraissement creerLotManuel(int nombreLapereaux, Long cageId, String notes) {
        Utilisateur u = utilisateurContextService.getUtilisateurConnecte();

        Cage cage = cageId != null
                ? cageRepository.findById(cageId).orElse(null)
                : cageRepository.findFirstByUtilisateurAndTypeAndStatut(
                        u, Cage.TypeCage.ENGRAISSEMENT, Cage.StatutCage.LIBRE).orElse(null);

        LotEngraissement lot = LotEngraissement.builder()
                .portee(null)
                .cage(cage)
                .nombreInitial(nombreLapereaux)
                .nombreActuel(nombreLapereaux)
                .nombreDeces(0)
                .dateEntree(LocalDate.now())
                .statut(LotEngraissement.StatutLot.EN_COURS)
                // ✅ Le lot appartient à l'utilisateur connecté
                .utilisateur(u)
                .notes(notes != null ? notes : "Lot créé manuellement")
                .build();

        if (cage != null) {
            cage.setStatut(Cage.StatutCage.OCCUPEE);
            cageRepository.save(cage);
        }

        return lotRepository.save(lot);
    }
}

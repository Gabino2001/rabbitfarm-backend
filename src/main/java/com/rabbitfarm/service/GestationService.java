package com.rabbitfarm.service;

import com.rabbitfarm.model.*;
import com.rabbitfarm.repository.GestationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GestationService {

    private final GestationRepository gestationRepository;

    public List<Gestation> findAll() {
        return gestationRepository.findAll();
    }

    public Gestation findById(Long id) {
        return gestationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gestation introuvable : " + id));
    }

    public List<Gestation> findEnCours() {
        return gestationRepository.findEnCoursOrdered();
    }

    public List<Gestation> findEnRetard() {
        return gestationRepository.findEnRetard(LocalDate.now());
    }

    public List<Gestation> findProchesDuTerme() {
        return gestationRepository.findProchesDuTerme(LocalDate.now(), LocalDate.now().plusDays(3));
    }

    // =====================================================
    // ENREGISTRER UNE NOUVELLE GESTATION
    // =====================================================
    public Gestation save(Gestation gestation) {
        // Empêcher deux gestations en cours pour la même lapine
        if (gestationRepository.lapineDejaGestante(gestation.getLapine().getId())) {
            throw new IllegalStateException(
                "Cette lapine a déjà une gestation en cours. Terminez-la avant d'en créer une nouvelle.");
        }

        // Calcul automatique de la date prévue (saillie + 31 jours)
        gestation.calculerDatePrevue();

        // Mettre la lapine en statut GESTANTE
        Lapin lapine = gestation.getLapine();
        lapine.setStatut(Lapin.StatutLapin.GESTANTE);

        return gestationRepository.save(gestation);
    }

    public Gestation update(Long id, Gestation gestationData) {
        Gestation gestation = findById(id);
        gestation.setDateSaillie(gestationData.getDateSaillie());
        gestation.calculerDatePrevue(); // recalcul si la date de saillie change
        gestation.setMale(gestationData.getMale());
        gestation.setNotes(gestationData.getNotes());
        return gestationRepository.save(gestation);
    }

    // =====================================================
    // ANNULER / MARQUER ÉCHEC d'une gestation
    // (fausse gestation, avortement, erreur de saisie)
    // =====================================================
    public Gestation marquerEchec(Long id, String motif) {
        Gestation gestation = findById(id);
        gestation.setStatut(Gestation.StatutGestation.ECHEC);
        gestation.setNotes((gestation.getNotes() != null ? gestation.getNotes() + " | " : "")
                + "Échec : " + motif);

        // Remettre la lapine en statut actif
        gestation.getLapine().setStatut(Lapin.StatutLapin.ACTIF);

        return gestationRepository.save(gestation);
    }

    public void delete(Long id) {
        gestationRepository.deleteById(id);
    }

    // =====================================================
    // MARQUER LA GESTATION COMME "MISE_BAS"
    // (appelé automatiquement quand la portée est créée)
    // =====================================================
    public void marquerMiseBas(Gestation gestation) {
        gestation.setStatut(Gestation.StatutGestation.MISE_BAS);
        gestationRepository.save(gestation);
    }

    // Stats
    public long countEnCours() {
        return gestationRepository.countByStatut(Gestation.StatutGestation.EN_COURS);
    }
}

package com.rabbitfarm.service;

import com.rabbitfarm.model.*;
import com.rabbitfarm.repository.GestationRepository;
import com.rabbitfarm.repository.LapinRepository;
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
    private final LapinRepository lapinRepository;


    // ===========================
    // LISTE COMPLETE
    // ===========================
    public List<Gestation> findAll() {
        return gestationRepository.findAll();
    }


    // ===========================
    // RECHERCHE PAR ID
    // ===========================
    public Gestation findById(Long id) {
        return gestationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Gestation introuvable : " + id
                        ));
    }


    // ===========================
    // GESTATIONS EN COURS
    // ===========================
    public List<Gestation> findEnCours() {
        return gestationRepository.findEnCoursOrdered();
    }


    // ===========================
    // GESTATIONS EN RETARD
    // ===========================
    public List<Gestation> findEnRetard() {
        return gestationRepository.findEnRetard(LocalDate.now());
    }


    // ===========================
    // PROCHES DE LA MISE BAS
    // ===========================
    public List<Gestation> findProchesDuTerme() {
        return gestationRepository.findProchesDuTerme(
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
    }


    // ===========================
    // CREER UNE GESTATION
    // ===========================
    public Gestation save(Gestation gestation) {

        if (gestationRepository.lapineDejaGestante(
                gestation.getLapine().getId())) {

            throw new IllegalStateException(
                    "Cette lapine a déjà une gestation en cours."
            );
        }


        gestation.calculerDatePrevue();


        Lapin lapine = gestation.getLapine();
        lapine.setStatut(
                Lapin.StatutLapin.GESTANTE
        );


        if (gestation.getStatut() == null) {
            gestation.setStatut(
                    Gestation.StatutGestation.EN_COURS
            );
        }


        return gestationRepository.save(gestation);
    }


    // ===========================
    // MODIFIER UNE GESTATION
    // ===========================
    public Gestation update(Long id, Gestation gestationData) {

        Gestation gestation = findById(id);


        gestation.setDateSaillie(
                gestationData.getDateSaillie()
        );


        // Modifier la lapine
        if (gestationData.getLapine() != null
                && gestationData.getLapine().getId() != null) {

            Lapin lapine = lapinRepository.findById(
                    gestationData.getLapine().getId()
            ).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Lapine introuvable"
                    ));

            gestation.setLapine(lapine);
        }


        // Modifier le mâle
        if (gestationData.getMale() != null
                && gestationData.getMale().getId() != null) {

            Lapin male = lapinRepository.findById(
                    gestationData.getMale().getId()
            ).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Mâle introuvable"
                    ));

            gestation.setMale(male);

        } else {
            gestation.setMale(null);
        }


        gestation.setNotes(
                gestationData.getNotes()
        );


        if (gestation.getStatut() == null) {
            gestation.setStatut(
                    Gestation.StatutGestation.EN_COURS
            );
        }


        gestation.calculerDatePrevue();


        return gestationRepository.save(gestation);
    }


    // ===========================
    // MARQUER ECHEC
    // ===========================
    public Gestation marquerEchec(Long id, String motif) {

        Gestation gestation = findById(id);

        gestation.setStatut(
                Gestation.StatutGestation.ECHEC
        );


        gestation.setNotes(
                (gestation.getNotes() != null
                        ? gestation.getNotes() + " | "
                        : "")
                        + "Échec : " + motif
        );


        gestation.getLapine().setStatut(
                Lapin.StatutLapin.ACTIF
        );


        return gestationRepository.save(gestation);
    }


    // ===========================
    // MARQUER MISE BAS
    // ===========================
    public void marquerMiseBas(Gestation gestation) {

        Gestation vraieGestation = findById(gestation.getId());

        vraieGestation.setStatut(
                Gestation.StatutGestation.MISE_BAS
        );

        gestationRepository.save(vraieGestation);
    }


    // ===========================
    // SUPPRIMER
    // ===========================
    public void delete(Long id) {
        gestationRepository.deleteById(id);
    }


    // ===========================
    // STATISTIQUES
    // ===========================
    public long countEnCours() {

        return gestationRepository.countByStatut(
                Gestation.StatutGestation.EN_COURS
        );
    }
}
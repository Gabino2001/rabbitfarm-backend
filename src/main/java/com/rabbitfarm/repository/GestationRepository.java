package com.rabbitfarm.repository;

import com.rabbitfarm.model.Gestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GestationRepository extends JpaRepository<Gestation, Long> {

    List<Gestation> findByStatut(Gestation.StatutGestation statut);

    List<Gestation> findByLapineId(Long lapineId);

    // Gestations en cours, triées par date de mise bas prévue (les plus proches en premier)
    @Query("SELECT g FROM Gestation g WHERE g.statut = 'EN_COURS' ORDER BY g.dateMiseBasPrevue ASC")
    List<Gestation> findEnCoursOrdered();

    // Gestations en retard (date prévue dépassée, toujours en cours)
    @Query("SELECT g FROM Gestation g WHERE g.statut = 'EN_COURS' AND g.dateMiseBasPrevue < :aujourdHui")
    List<Gestation> findEnRetard(LocalDate aujourdHui);

    // Gestations proches du terme (dans les X prochains jours)
    @Query("SELECT g FROM Gestation g WHERE g.statut = 'EN_COURS' AND g.dateMiseBasPrevue BETWEEN :debut AND :fin")
    List<Gestation> findProchesDuTerme(LocalDate debut, LocalDate fin);

    // Vérifie si une lapine a déjà une gestation en cours (évite les doublons)
    @Query("SELECT COUNT(g) > 0 FROM Gestation g WHERE g.lapine.id = :lapineId AND g.statut = 'EN_COURS'")
    boolean lapineDejaGestante(Long lapineId);

    long countByStatut(Gestation.StatutGestation statut);
}

package com.rabbitfarm.repository;

import com.rabbitfarm.model.Portee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PorteeRepository extends JpaRepository<Portee, Long> {

    List<Portee> findByLapineId(Long lapineId);
    List<Portee> findByStatut(Portee.StatutPortee statut);
    List<Portee> findByDateMiseBasBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT SUM(p.nbNes) FROM Portee p")
    Integer sumTotalNes();

    @Query("SELECT SUM(p.nbDeces) FROM Portee p")
    Integer sumTotalDeces();

    @Query("SELECT SUM(p.nbNes) FROM Portee p WHERE p.dateMiseBas >= :debut AND p.dateMiseBas <= :fin")
    Integer sumNesEntreDates(LocalDate debut, LocalDate fin);

    @Query("SELECT p FROM Portee p WHERE p.dateSevrage <= :date AND p.statut = 'EN_COURS'")
    List<Portee> findSevragesAFaire(LocalDate date);

    long countByLapineId(Long lapineId);

    // =====================================================
    // NOUVEAU : portées prêtes pour le sevrage automatique
    // = date de sevrage dépassée + pas encore sevrée + lapereaux vivants
    // =====================================================
    @Query("""
        SELECT p FROM Portee p
        WHERE p.dateSevrage <= :date
        AND p.sevree = false
        AND p.statut = 'EN_COURS'
        AND p.nbVivants > 0
    """)
    List<Portee> findPorteesASeurer(LocalDate date);
}

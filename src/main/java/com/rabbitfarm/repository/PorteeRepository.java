package com.rabbitfarm.repository;

import com.rabbitfarm.model.Portee;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PorteeRepository extends JpaRepository<Portee, Long> {

    // ✅ Filtres par utilisateur
    List<Portee> findByUtilisateur(Utilisateur utilisateur);

    List<Portee> findByUtilisateurAndStatut(Utilisateur utilisateur, Portee.StatutPortee statut);

    @Query("SELECT SUM(p.nbNes) FROM Portee p WHERE p.utilisateur = :u")
    Integer sumTotalNes(@Param("u") Utilisateur utilisateur);

    @Query("SELECT SUM(p.nbDeces) FROM Portee p WHERE p.utilisateur = :u")
    Integer sumTotalDeces(@Param("u") Utilisateur utilisateur);

    @Query("SELECT SUM(p.nbNes) FROM Portee p WHERE p.utilisateur = :u AND p.dateMiseBas >= :debut AND p.dateMiseBas <= :fin")
    Integer sumNesEntreDates(@Param("u") Utilisateur utilisateur, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT p FROM Portee p WHERE p.utilisateur = :u AND p.dateSevrage <= :date AND p.statut = 'EN_COURS'")
    List<Portee> findSevragesAFaire(@Param("u") Utilisateur utilisateur, @Param("date") LocalDate date);

    @Query("""
        SELECT p FROM Portee p
        WHERE p.dateSevrage <= :date
        AND p.sevree = false
        AND p.statut = 'EN_COURS'
        AND p.nbVivants > 0
    """)
    List<Portee> findPorteesASeurer(@Param("date") LocalDate date);

    List<Portee> findByLapineId(Long lapineId);
    List<Portee> findByStatut(Portee.StatutPortee statut);
    long countByLapineId(Long lapineId);
}

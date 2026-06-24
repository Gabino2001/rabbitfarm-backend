package com.rabbitfarm.repository;

import com.rabbitfarm.model.Gestation;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GestationRepository extends JpaRepository<Gestation, Long> {

    // ✅ Filtres par utilisateur
    List<Gestation> findByUtilisateur(Utilisateur utilisateur);

    @Query("SELECT g FROM Gestation g WHERE g.utilisateur = :u AND g.statut = 'EN_COURS' ORDER BY g.dateMiseBasPrevue ASC")
    List<Gestation> findEnCoursOrdered(@Param("u") Utilisateur utilisateur);

    @Query("SELECT g FROM Gestation g WHERE g.utilisateur = :u AND g.statut = 'EN_COURS' AND g.dateMiseBasPrevue < :aujourdHui")
    List<Gestation> findEnRetard(@Param("u") Utilisateur utilisateur, @Param("aujourdHui") LocalDate aujourdHui);

    @Query("SELECT g FROM Gestation g WHERE g.utilisateur = :u AND g.statut = 'EN_COURS' AND g.dateMiseBasPrevue BETWEEN :debut AND :fin")
    List<Gestation> findProchesDuTerme(@Param("u") Utilisateur utilisateur, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(g) > 0 FROM Gestation g WHERE g.lapine.id = :lapineId AND g.statut = 'EN_COURS'")
    boolean lapineDejaGestante(@Param("lapineId") Long lapineId);

    @Query("SELECT COUNT(g) FROM Gestation g WHERE g.utilisateur = :u AND g.statut = :statut")
    long countByUtilisateurAndStatut(@Param("u") Utilisateur utilisateur, @Param("statut") Gestation.StatutGestation statut);

    List<Gestation> findByStatut(Gestation.StatutGestation statut);
    List<Gestation> findByLapineId(Long lapineId);
    long countByStatut(Gestation.StatutGestation statut);
}

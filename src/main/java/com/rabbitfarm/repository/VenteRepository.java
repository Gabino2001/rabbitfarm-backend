package com.rabbitfarm.repository;

import com.rabbitfarm.model.Utilisateur;
import com.rabbitfarm.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    // ✅ Filtres par utilisateur
    List<Vente> findByUtilisateur(Utilisateur utilisateur);

    @Query("SELECT SUM(v.prixTotal) FROM Vente v WHERE v.utilisateur = :u")
    Double sumTotalRevenus(@Param("u") Utilisateur utilisateur);

    @Query("SELECT SUM(v.prixTotal) FROM Vente v WHERE v.utilisateur = :u AND v.dateVente >= :debut AND v.dateVente <= :fin")
    Double sumRevenusEntreDates(@Param("u") Utilisateur utilisateur, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(v.poidsKg) FROM Vente v WHERE v.utilisateur = :u AND v.dateVente >= :debut AND v.dateVente <= :fin")
    Double sumPoidsEntreDates(@Param("u") Utilisateur utilisateur, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    List<Vente> findByUtilisateurAndDateVenteBetween(Utilisateur utilisateur, LocalDate debut, LocalDate fin);
}

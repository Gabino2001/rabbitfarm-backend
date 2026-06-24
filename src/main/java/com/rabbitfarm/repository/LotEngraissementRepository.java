package com.rabbitfarm.repository;

import com.rabbitfarm.model.LotEngraissement;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotEngraissementRepository extends JpaRepository<LotEngraissement, Long> {

    // ✅ Filtres par utilisateur
    List<LotEngraissement> findByUtilisateur(Utilisateur utilisateur);

    List<LotEngraissement> findByUtilisateurAndStatut(Utilisateur utilisateur, LotEngraissement.StatutLot statut);

    @Query("SELECT COALESCE(SUM(l.nombreActuel), 0) FROM LotEngraissement l WHERE l.utilisateur = :u AND l.statut = 'EN_COURS'")
    Integer sumLapereauEnCours(@Param("u") Utilisateur utilisateur);

    @Query("SELECT COUNT(l) FROM LotEngraissement l WHERE l.utilisateur = :u AND l.statut = :statut")
    long countByUtilisateurAndStatut(@Param("u") Utilisateur utilisateur, @Param("statut") LotEngraissement.StatutLot statut);

    Optional<LotEngraissement> findByPorteeId(Long porteeId);

    // Conservés pour compatibilité interne
    List<LotEngraissement> findByStatut(LotEngraissement.StatutLot statut);
    long countByStatut(LotEngraissement.StatutLot statut);

    @Query("SELECT COALESCE(SUM(l.nombreActuel), 0) FROM LotEngraissement l WHERE l.statut = 'EN_COURS'")
    Integer sumLapereauEnCours();
}

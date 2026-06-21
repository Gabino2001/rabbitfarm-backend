package com.rabbitfarm.repository;

import com.rabbitfarm.model.LotEngraissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotEngraissementRepository extends JpaRepository<LotEngraissement, Long> {

    // Tous les lots en cours
    List<LotEngraissement> findByStatut(LotEngraissement.StatutLot statut);

    // Retrouver le lot lié à une portée
    Optional<LotEngraissement> findByPorteeId(Long porteeId);

    // Nombre total de lapereaux actuellement en engraissement
    @Query("SELECT COALESCE(SUM(l.nombreActuel), 0) FROM LotEngraissement l WHERE l.statut = 'EN_COURS'")
    Integer sumLapereauEnCours();

    // Nombre total de lots en cours
    long countByStatut(LotEngraissement.StatutLot statut);
}

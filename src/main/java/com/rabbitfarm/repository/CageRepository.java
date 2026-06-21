package com.rabbitfarm.repository;

import com.rabbitfarm.model.Cage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CageRepository extends JpaRepository<Cage, Long> {

    List<Cage> findByStatut(Cage.StatutCage statut);

    List<Cage> findByType(Cage.TypeCage type);

    long countByStatut(Cage.StatutCage statut);

    // NOUVEAU : trouver la première cage libre d'un type donné
    // Utilisé pour affecter automatiquement une cage au sevrage
    Optional<Cage> findFirstByTypeAndStatut(Cage.TypeCage type, Cage.StatutCage statut);
}

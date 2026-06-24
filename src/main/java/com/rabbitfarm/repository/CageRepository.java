package com.rabbitfarm.repository;

import com.rabbitfarm.model.Cage;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CageRepository extends JpaRepository<Cage, Long> {

    // ✅ Filtres par utilisateur
    List<Cage> findByUtilisateur(Utilisateur utilisateur);

    List<Cage> findByUtilisateurAndStatut(Utilisateur utilisateur, Cage.StatutCage statut);

    List<Cage> findByUtilisateurAndType(Utilisateur utilisateur, Cage.TypeCage type);

    long countByUtilisateur(Utilisateur utilisateur);

    long countByUtilisateurAndStatut(Utilisateur utilisateur, Cage.StatutCage statut);

    // Pour le sevrage automatique : cherche une cage libre d'un type donné POUR un utilisateur
    Optional<Cage> findFirstByUtilisateurAndTypeAndStatut(Utilisateur utilisateur, Cage.TypeCage type, Cage.StatutCage statut);

    // Conservé pour compatibilité interne
    List<Cage> findByStatut(Cage.StatutCage statut);
    List<Cage> findByType(Cage.TypeCage type);
    long countByStatut(Cage.StatutCage statut);
    Optional<Cage> findFirstByTypeAndStatut(Cage.TypeCage type, Cage.StatutCage statut);
}

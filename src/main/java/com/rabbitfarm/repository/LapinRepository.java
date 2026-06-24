package com.rabbitfarm.repository;

import com.rabbitfarm.model.Lapin;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LapinRepository extends JpaRepository<Lapin, Long> {

    // ✅ Filtres par utilisateur
    List<Lapin> findByUtilisateur(Utilisateur utilisateur);

    List<Lapin> findByUtilisateurAndType(Utilisateur utilisateur, Lapin.TypeLapin type);

    List<Lapin> findByUtilisateurAndSexe(Utilisateur utilisateur, Lapin.Sexe sexe);

    List<Lapin> findByUtilisateurAndSexeAndType(Utilisateur utilisateur, Lapin.Sexe sexe, Lapin.TypeLapin type);

    List<Lapin> findByUtilisateurAndStatut(Utilisateur utilisateur, Lapin.StatutLapin statut);

    long countByUtilisateur(Utilisateur utilisateur);

    long countByUtilisateurAndType(Utilisateur utilisateur, Lapin.TypeLapin type);

    @Query("SELECT COUNT(l) FROM Lapin l WHERE l.utilisateur = :u AND l.sexe = 'MALE' AND l.type = 'REPRODUCTEUR'")
    long countMalesReproducteurs(@Param("u") Utilisateur utilisateur);

    @Query("SELECT COUNT(l) FROM Lapin l WHERE l.utilisateur = :u AND l.sexe = 'FEMELLE' AND l.type = 'REPRODUCTEUR'")
    long countFemellesReproductrices(@Param("u") Utilisateur utilisateur);

    // Conservés pour compatibilité interne (ex: GestationService qui cherche par id)
    List<Lapin> findBySexe(Lapin.Sexe sexe);
    List<Lapin> findByType(Lapin.TypeLapin type);
    List<Lapin> findByStatut(Lapin.StatutLapin statut);
    List<Lapin> findBySexeAndType(Lapin.Sexe sexe, Lapin.TypeLapin type);
}

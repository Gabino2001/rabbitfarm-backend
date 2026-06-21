package com.rabbitfarm.repository;

import com.rabbitfarm.model.Lapin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LapinRepository extends JpaRepository<Lapin, Long> {

    List<Lapin> findBySexe(Lapin.Sexe sexe);

    List<Lapin> findByType(Lapin.TypeLapin type);

    List<Lapin> findByStatut(Lapin.StatutLapin statut);

    List<Lapin> findBySexeAndType(Lapin.Sexe sexe, Lapin.TypeLapin type);

    long countBySexe(Lapin.Sexe sexe);

    long countByType(Lapin.TypeLapin type);

    long countByStatut(Lapin.StatutLapin statut);

    @Query("SELECT COUNT(l) FROM Lapin l WHERE l.sexe = 'MALE' AND l.type = 'REPRODUCTEUR'")
    long countMalesReproducteurs();

    @Query("SELECT COUNT(l) FROM Lapin l WHERE l.sexe = 'FEMELLE' AND l.type = 'REPRODUCTEUR'")
    long countFemellesReproductrices();
}

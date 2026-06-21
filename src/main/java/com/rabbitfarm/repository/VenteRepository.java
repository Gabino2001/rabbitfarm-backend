package com.rabbitfarm.repository;

import com.rabbitfarm.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByDateVenteBetween(LocalDate debut, LocalDate fin);

    List<Vente> findByClient(String client);

    @Query("SELECT SUM(v.prixTotal) FROM Vente v")
    Double sumTotalRevenus();

    @Query("SELECT SUM(v.prixTotal) FROM Vente v WHERE v.dateVente >= :debut AND v.dateVente <= :fin")
    Double sumRevenusEntreDates(LocalDate debut, LocalDate fin);

    @Query("SELECT SUM(v.poidsKg) FROM Vente v WHERE v.dateVente >= :debut AND v.dateVente <= :fin")
    Double sumPoidsEntreDates(LocalDate debut, LocalDate fin);
}

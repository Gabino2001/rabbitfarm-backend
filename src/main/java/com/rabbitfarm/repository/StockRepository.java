package com.rabbitfarm.repository;

import com.rabbitfarm.model.Stock;
import com.rabbitfarm.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // ✅ Filtres par utilisateur
    List<Stock> findByUtilisateur(Utilisateur utilisateur);

    @Query("SELECT s FROM Stock s WHERE s.utilisateur = :u AND s.quantite <= s.seuilAlerte")
    List<Stock> findStocksBas(@Param("u") Utilisateur utilisateur);

    @Query("SELECT s FROM Stock s WHERE s.utilisateur = :u AND s.quantite <= s.seuilAlerte * 0.5")
    List<Stock> findStocksCritiques(@Param("u") Utilisateur utilisateur);
}

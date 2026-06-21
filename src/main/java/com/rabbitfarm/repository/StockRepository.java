package com.rabbitfarm.repository;

import com.rabbitfarm.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.seuilAlerte")
    List<Stock> findStocksBas();

    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.seuilAlerte * 0.5")
    List<Stock> findStocksCritiques();
}

package com.rabbitfarm.service;

import com.rabbitfarm.model.Stock;
import com.rabbitfarm.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock introuvable avec l'id : " + id));
    }

    public List<Stock> findStocksBas() {
        return stockRepository.findStocksBas();
    }

    public List<Stock> findStocksCritiques() {
        return stockRepository.findStocksCritiques();
    }

    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock update(Long id, Stock stockData) {
        Stock stock = findById(id);
        stock.setAliment(stockData.getAliment());
        stock.setQuantite(stockData.getQuantite());
        stock.setUnite(stockData.getUnite());
        stock.setSeuilAlerte(stockData.getSeuilAlerte());
        stock.setFournisseur(stockData.getFournisseur());
        stock.setNotes(stockData.getNotes());
        return stockRepository.save(stock);
    }

    public Stock ajouterQuantite(Long id, Double quantite) {
        Stock stock = findById(id);
        stock.setQuantite(stock.getQuantite() + quantite);
        return stockRepository.save(stock);
    }

    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    public long countAlertes() {
        return findStocksBas().size();
    }
}

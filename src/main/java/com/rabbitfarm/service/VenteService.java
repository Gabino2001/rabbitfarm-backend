package com.rabbitfarm.service;

import com.rabbitfarm.model.Vente;
import com.rabbitfarm.repository.VenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VenteService {

    private final VenteRepository venteRepository;

    public List<Vente> findAll() {
        return venteRepository.findAll();
    }

    public Vente findById(Long id) {
        return venteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vente introuvable avec l'id : " + id));
    }

    public Vente save(Vente vente) {
        return venteRepository.save(vente);
    }

    public Vente update(Long id, Vente venteData) {
        Vente vente = findById(id);
        vente.setDateVente(venteData.getDateVente());
        vente.setClient(venteData.getClient());
        vente.setPoidsKg(venteData.getPoidsKg());
        vente.setPrixTotal(venteData.getPrixTotal());
        vente.setType(venteData.getType());
        vente.setNombreTetes(venteData.getNombreTetes());
        vente.setNotes(venteData.getNotes());
        return venteRepository.save(vente);
    }

    public void delete(Long id) {
        venteRepository.deleteById(id);
    }

    public double getRevenusTotaux() {
        Double total = venteRepository.sumTotalRevenus();
        return total != null ? total : 0;
    }

    public double getRevenusDuMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        Double total = venteRepository.sumRevenusEntreDates(debut, fin);
        return total != null ? total : 0;
    }

    public double getPoidsDuMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        Double total = venteRepository.sumPoidsEntreDates(debut, fin);
        return total != null ? total : 0;
    }
}

package com.rabbitfarm.service;

import com.rabbitfarm.model.Lapin;
import com.rabbitfarm.repository.LapinRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LapinService {

    private final LapinRepository lapinRepository;

    public List<Lapin> findAll() {
        return lapinRepository.findAll();
    }

    public Lapin findById(Long id) {
        return lapinRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lapin introuvable avec l'id : " + id));
    }

    public List<Lapin> findReproducteurs() {
        return lapinRepository.findByType(Lapin.TypeLapin.REPRODUCTEUR);
    }

    public List<Lapin> findEngraissement() {
        return lapinRepository.findByType(Lapin.TypeLapin.ENGRAISSEMENT);
    }

    public List<Lapin> findMalesReproducteurs() {
        return lapinRepository.findBySexeAndType(Lapin.Sexe.MALE, Lapin.TypeLapin.REPRODUCTEUR);
    }

    public List<Lapin> findFemellesReproductrices() {
        return lapinRepository.findBySexeAndType(Lapin.Sexe.FEMELLE, Lapin.TypeLapin.REPRODUCTEUR);
    }

    public Lapin save(Lapin lapin) {
        return lapinRepository.save(lapin);
    }

    public Lapin update(Long id, Lapin lapinData) {
        Lapin lapin = findById(id);
        lapin.setNom(lapinData.getNom());
        lapin.setRace(lapinData.getRace());
        lapin.setSexe(lapinData.getSexe());
        lapin.setDateNaissance(lapinData.getDateNaissance());
        lapin.setPoidKg(lapinData.getPoidKg());
        lapin.setStatut(lapinData.getStatut());
        lapin.setType(lapinData.getType());
        lapin.setNotes(lapinData.getNotes());
        return lapinRepository.save(lapin);
    }

    public void delete(Long id) {
        lapinRepository.deleteById(id);
    }

    // Stats
    public long countTotal() {
        return lapinRepository.count();
    }

    public long countMalesReproducteurs() {
        return lapinRepository.countMalesReproducteurs();
    }

    public long countFemellesReproductrices() {
        return lapinRepository.countFemellesReproductrices();
    }

    public long countEngraissement() {
        return lapinRepository.countByType(Lapin.TypeLapin.ENGRAISSEMENT);
    }
}

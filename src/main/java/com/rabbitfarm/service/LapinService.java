package com.rabbitfarm.service;

import com.rabbitfarm.model.Lapin;
import com.rabbitfarm.model.Utilisateur;
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
    private final UtilisateurContextService utilisateurContextService;

    public List<Lapin> findAll() {
        return lapinRepository.findByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public Lapin findById(Long id) {
        return lapinRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lapin introuvable avec l'id : " + id));
    }

    public List<Lapin> findReproducteurs() {
        return lapinRepository.findByUtilisateurAndType(
                utilisateurContextService.getUtilisateurConnecte(), Lapin.TypeLapin.REPRODUCTEUR);
    }

    public List<Lapin> findEngraissement() {
        return lapinRepository.findByUtilisateurAndType(
                utilisateurContextService.getUtilisateurConnecte(), Lapin.TypeLapin.ENGRAISSEMENT);
    }

    public List<Lapin> findMalesReproducteurs() {
        return lapinRepository.findByUtilisateurAndSexeAndType(
                utilisateurContextService.getUtilisateurConnecte(),
                Lapin.Sexe.MALE, Lapin.TypeLapin.REPRODUCTEUR);
    }

    public List<Lapin> findFemellesReproductrices() {
        return lapinRepository.findByUtilisateurAndSexeAndType(
                utilisateurContextService.getUtilisateurConnecte(),
                Lapin.Sexe.FEMELLE, Lapin.TypeLapin.REPRODUCTEUR);
    }

    public Lapin save(Lapin lapin) {
        // ✅ On rattache le lapin à l'utilisateur connecté
        lapin.setUtilisateur(utilisateurContextService.getUtilisateurConnecte());
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
        return lapinRepository.countByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public long countMalesReproducteurs() {
        return lapinRepository.countMalesReproducteurs(utilisateurContextService.getUtilisateurConnecte());
    }

    public long countFemellesReproductrices() {
        return lapinRepository.countFemellesReproductrices(utilisateurContextService.getUtilisateurConnecte());
    }

    public long countEngraissement() {
        return lapinRepository.countByUtilisateurAndType(
                utilisateurContextService.getUtilisateurConnecte(), Lapin.TypeLapin.ENGRAISSEMENT);
    }
}

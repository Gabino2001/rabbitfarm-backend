package com.rabbitfarm.service;

import com.rabbitfarm.model.Cage;
import com.rabbitfarm.model.Utilisateur;
import com.rabbitfarm.repository.CageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CageService {

    private final CageRepository cageRepository;
    private final UtilisateurContextService utilisateurContextService;

    public List<Cage> findAll() {
        return cageRepository.findByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public Cage findById(Long id) {
        return cageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cage introuvable avec l'id : " + id));
    }

    public List<Cage> findLibres() {
        return cageRepository.findByUtilisateurAndStatut(
                utilisateurContextService.getUtilisateurConnecte(), Cage.StatutCage.LIBRE);
    }

    public Cage save(Cage cage) {
        // ✅ On rattache la cage à l'utilisateur connecté
        cage.setUtilisateur(utilisateurContextService.getUtilisateurConnecte());
        return cageRepository.save(cage);
    }

    public Cage update(Long id, Cage cageData) {
        Cage cage = findById(id);
        cage.setNumero(cageData.getNumero());
        cage.setType(cageData.getType());
        cage.setStatut(cageData.getStatut());
        cage.setCapaciteMax(cageData.getCapaciteMax());
        cage.setLocalisation(cageData.getLocalisation());
        cage.setNotes(cageData.getNotes());
        return cageRepository.save(cage);
    }

    public void delete(Long id) {
        cageRepository.deleteById(id);
    }

    public long countTotal() {
        return cageRepository.countByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public long countLibres() {
        return cageRepository.countByUtilisateurAndStatut(
                utilisateurContextService.getUtilisateurConnecte(), Cage.StatutCage.LIBRE);
    }
}

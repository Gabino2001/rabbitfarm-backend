package com.rabbitfarm.service;

import com.rabbitfarm.model.Gestation;
import com.rabbitfarm.model.Portee;
import com.rabbitfarm.repository.PorteeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PorteeService {

    private final PorteeRepository porteeRepository;
    private final GestationService gestationService;
    private final SevrageService sevrageService;
    private final UtilisateurContextService utilisateurContextService;

    public List<Portee> findAll() {
        return porteeRepository.findByUtilisateur(utilisateurContextService.getUtilisateurConnecte());
    }

    public Portee findById(Long id) {
        return porteeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portée introuvable avec l'id : " + id));
    }

    public List<Portee> findByLapine(Long lapineId) {
        return porteeRepository.findByLapineId(lapineId);
    }

    public List<Portee> findEnCours() {
        return porteeRepository.findByUtilisateurAndStatut(
                utilisateurContextService.getUtilisateurConnecte(), Portee.StatutPortee.EN_COURS);
    }

    public Portee preparerDepuisGestation(Long gestationId) {
        Gestation gestation = gestationService.findById(gestationId);

        if (gestation.getStatut() != Gestation.StatutGestation.EN_COURS) {
            throw new IllegalStateException("Cette gestation n'est plus en cours.");
        }

        Portee portee = new Portee();
        portee.setLapine(gestation.getLapine());
        portee.setDateMiseBas(LocalDate.now());
        portee.setGestation(gestation);
        portee.setNotes("Issue de la gestation #" + gestation.getId()
                + " (saillie le " + gestation.getDateSaillie() + ")");

        return portee;
    }

    public Portee save(Portee portee) {
        portee.setNbVivants(portee.getNbNes() - portee.getNbDeces());

        if (portee.getDateSevrage() == null) {
            portee.setDateSevrage(portee.getDateMiseBas().plusDays(45));
        }

        // ✅ On rattache la portée à l'utilisateur connecté
        portee.setUtilisateur(utilisateurContextService.getUtilisateurConnecte());

        Portee saved = porteeRepository.save(portee);

        if (portee.getGestation() != null && portee.getGestation().getId() != null) {
            gestationService.marquerMiseBas(portee.getGestation());
        }

        return saved;
    }

    public Portee update(Long id, Portee porteeData) {
        Portee portee = findById(id);
        portee.setNbNes(porteeData.getNbNes());
        portee.setNbDeces(porteeData.getNbDeces());
        portee.setNbVivants(porteeData.getNbNes() - porteeData.getNbDeces());
        portee.setDateSevrage(porteeData.getDateSevrage());
        portee.setStatut(porteeData.getStatut());
        portee.setNotes(porteeData.getNotes());
        return porteeRepository.save(portee);
    }

    public void delete(Long id) {
        porteeRepository.deleteById(id);
    }

    public Portee sevrerPortee(Long porteeId) {
        sevrageService.sevrerPortee(porteeId);
        return findById(porteeId);
    }

    public int getNesduMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        Integer total = porteeRepository.sumNesEntreDates(
                utilisateurContextService.getUtilisateurConnecte(), debut, fin);
        return total != null ? total : 0;
    }

    public List<Portee> getSevragesAVenir() {
        return porteeRepository.findSevragesAFaire(
                utilisateurContextService.getUtilisateurConnecte(),
                LocalDate.now().plusDays(7));
    }

    public int getTotalNes() {
        Integer total = porteeRepository.sumTotalNes(utilisateurContextService.getUtilisateurConnecte());
        return total != null ? total : 0;
    }

    public int getTotalDeces() {
        Integer total = porteeRepository.sumTotalDeces(utilisateurContextService.getUtilisateurConnecte());
        return total != null ? total : 0;
    }
}

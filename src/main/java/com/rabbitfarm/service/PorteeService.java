package com.rabbitfarm.service;

import com.rabbitfarm.model.Portee;
import com.rabbitfarm.model.Gestation;
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
    private final GestationService gestationService; // lien avec le suivi de gestation
    private final SevrageService sevrageService;      // lien avec le sevrage groupé (Lot)

    public List<Portee> findAll() {
        return porteeRepository.findAll();
    }

    public Portee findById(Long id) {
        return porteeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portée introuvable avec l'id : " + id));
    }

    public List<Portee> findByLapine(Long lapineId) {
        return porteeRepository.findByLapineId(lapineId);
    }

    public List<Portee> findEnCours() {
        return porteeRepository.findByStatut(Portee.StatutPortee.EN_COURS);
    }

    // =====================================================
    // PRÉ-REMPLIR une portée à partir d'une gestation
    // (utilisé par le bouton "Enregistrer mise bas")
    // =====================================================
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

    // =====================================================
    // CRÉATION d'une portée — gère aussi la clôture
    // de la gestation liée si elle existe
    // =====================================================
    public Portee save(Portee portee) {
        portee.setNbVivants(portee.getNbNes() - portee.getNbDeces());

        if (portee.getDateSevrage() == null) {
            portee.setDateSevrage(portee.getDateMiseBas().plusDays(45));
        }

        Portee saved = porteeRepository.save(portee);

        // Si cette portée vient d'une gestation suivie, on la clôture
        if (portee.getGestation() != null) {
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

    // =====================================================
    // SEVRAGE — approche A : LOT GROUPÉ
    // Délègue entièrement à SevrageService, qui crée
    // UNE ligne LotEngraissement (pas un Lapin par lapereau).
    // Conservé ici comme point d'entrée pratique depuis
    // l'écran "Portées".
    // =====================================================
    public Portee sevrerPortee(Long porteeId) {
        // SevrageService.sevrerPortee() :
        //  - crée le LotEngraissement (nombreInitial = nbVivants)
        //  - assigne une cage d'engraissement libre si dispo
        //  - marque portee.sevree = true et statut = SEVREE
        sevrageService.sevrerPortee(porteeId);
        return findById(porteeId);
    }

    // Stats du mois en cours
    public int getNesduMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        Integer total = porteeRepository.sumNesEntreDates(debut, fin);
        return total != null ? total : 0;
    }

    public List<Portee> getSevragesAVenir() {
        return porteeRepository.findSevragesAFaire(LocalDate.now().plusDays(7));
    }

    public int getTotalNes() {
        Integer total = porteeRepository.sumTotalNes();
        return total != null ? total : 0;
    }

    public int getTotalDeces() {
        Integer total = porteeRepository.sumTotalDeces();
        return total != null ? total : 0;
    }
}
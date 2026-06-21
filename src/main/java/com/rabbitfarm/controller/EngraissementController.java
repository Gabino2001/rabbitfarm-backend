package com.rabbitfarm.controller;

import com.rabbitfarm.model.LotEngraissement;
import com.rabbitfarm.service.CageService;
import com.rabbitfarm.service.SevrageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/engraissement")
@RequiredArgsConstructor
public class EngraissementController {

    private final SevrageService sevrageService;
    private final CageService cageService;


    // Liste de tous les lots
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("lotsEnCours", sevrageService.getLotsEnCours());
        model.addAttribute("tousLesLots", sevrageService.getTousLesLots());
        model.addAttribute("totalEnEngraissement", sevrageService.getTotalLapereauEnEngraissement());
        return "engraissement/liste";
    }


    // Sevrage manuel depuis la page portées
    @PostMapping("/sevrer/{porteeId}")
    public String sevrer(@PathVariable Long porteeId,
                         RedirectAttributes redirectAttributes) {

        try {
            LotEngraissement lot = sevrageService.sevrerPortee(porteeId);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    "✅ Sevrage effectué ! "
                            + lot.getNombreInitial()
                            + " lapereaux envoyés en engraissement (Lot #"
                            + lot.getId() + ")."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Erreur : " + e.getMessage());
        }

        return "redirect:/portees";
    }


    // Enregistrer un décès
    @PostMapping("/{lotId}/deces")
    public String enregistrerDeces(@PathVariable Long lotId,
                                   @RequestParam int nombreDeces,
                                   RedirectAttributes redirectAttributes) {

        try {
            sevrageService.enregistrerDeces(lotId, nombreDeces);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    nombreDeces + " décès enregistré(s)."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Erreur : " + e.getMessage());
        }

        return "redirect:/engraissement";
    }


    // Clôturer un lot
    @PostMapping("/{lotId}/cloturer")
    public String cloturerLot(@PathVariable Long lotId,
                              @RequestParam LotEngraissement.StatutLot statut,
                              RedirectAttributes redirectAttributes) {

        try {
            sevrageService.cloturerLot(lotId, statut);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    "Lot clôturé avec succès."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Erreur : " + e.getMessage());
        }

        return "redirect:/engraissement";
    }


    // Ajouter lapereaux à un lot existant
    @PostMapping("/{lotId}/ajouter")
    public String ajouterLapereaux(@PathVariable Long lotId,
                                   @RequestParam int nombre,
                                   @RequestParam(required = false) String motif,
                                   RedirectAttributes redirectAttributes) {

        try {
            sevrageService.ajouterLapereaux(lotId, nombre, motif);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    "✅ " + nombre + " lapereau(x) ajouté(s)."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Erreur : " + e.getMessage());
        }

        return "redirect:/engraissement";
    }


    // Formulaire création lot manuel
    @GetMapping("/nouveau")
    public String formulaireNouveauLot(Model model) {

        model.addAttribute("cages", cageService.findAll());

        return "engraissement/formulaire";
    }


    // Créer un lot manuel
    @PostMapping("/nouveau")
    public String creerLotManuel(@RequestParam int nombreLapereaux,
                                 @RequestParam(required = false) Long cageId,
                                 @RequestParam(required = false) String notes,
                                 RedirectAttributes redirectAttributes) {

        try {
            LotEngraissement lot =
                    sevrageService.creerLotManuel(nombreLapereaux, cageId, notes);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    "✅ Lot #" + lot.getId()
                            + " créé avec "
                            + nombreLapereaux
                            + " lapereaux."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Erreur : " + e.getMessage());
        }

        return "redirect:/engraissement";
    }
}
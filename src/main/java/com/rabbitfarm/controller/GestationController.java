package com.rabbitfarm.controller;

import com.rabbitfarm.model.Gestation;
import com.rabbitfarm.model.Portee;
import com.rabbitfarm.service.GestationService;
import com.rabbitfarm.service.LapinService;
import com.rabbitfarm.service.PorteeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestations")
@RequiredArgsConstructor
public class GestationController {

    private final GestationService gestationService;
    private final LapinService lapinService;
    private final PorteeService porteeService;

    // ===========================
    // LISTE
    // ===========================
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("gestationsEnCours", gestationService.findEnCours());
        model.addAttribute("enRetard", gestationService.findEnRetard());
        model.addAttribute("prochesDuTerme", gestationService.findProchesDuTerme());
        model.addAttribute("totalEnCours", gestationService.countEnCours());
        return "gestations/liste";
    }

    // ===========================
    // NOUVELLE GESTATION
    // ===========================
    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("gestation", new Gestation());
        model.addAttribute("femelles", lapinService.findFemellesReproductrices());
        model.addAttribute("males", lapinService.findMalesReproducteurs());
        return "gestations/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Gestation gestation,
                        BindingResult result, Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("femelles", lapinService.findFemellesReproductrices());
            model.addAttribute("males", lapinService.findMalesReproducteurs());
            return "gestations/formulaire";
        }
        try {
            gestationService.save(gestation);
            redirectAttributes.addFlashAttribute("succes",
                "✅ Gestation enregistrée ! Mise bas prévue le "
                + gestation.getDateMiseBasPrevue() + ".");
        } catch (IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("femelles", lapinService.findFemellesReproductrices());
            model.addAttribute("males", lapinService.findMalesReproducteurs());
            return "gestations/formulaire";
        }
        return "redirect:/gestations";
    }

    // ===========================
    // MODIFIER
    // ===========================
    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        model.addAttribute("gestation", gestationService.findById(id));
        model.addAttribute("femelles", lapinService.findFemellesReproductrices());
        model.addAttribute("males", lapinService.findMalesReproducteurs());
        return "gestations/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Gestation gestation,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {


        gestation.setId(id);


        if (result.hasErrors()) {

            model.addAttribute("femelles",
                    lapinService.findFemellesReproductrices());

            model.addAttribute("males",
                    lapinService.findMalesReproducteurs());

            return "gestations/formulaire";
        }


        gestationService.update(id, gestation);


        redirectAttributes.addFlashAttribute(
                "succes",
                "Gestation modifiée !"
        );


        return "redirect:/gestations";
    }

    // ===========================
    // ✅ BOUTON CLÉ : "Enregistrer mise bas"
    // Redirige vers le formulaire portée PRÉ-REMPLI
    // ===========================
    @GetMapping("/{id}/mise-bas")
    public String enregistrerMiseBas(@PathVariable Long id, Model model) {
        Portee portee = porteeService.preparerDepuisGestation(id);
        model.addAttribute("portee", portee);
        model.addAttribute("lapines", lapinService.findFemellesReproductrices());
        model.addAttribute("statuts", Portee.StatutPortee.values());
        model.addAttribute("depuisGestation", true); // pour adapter le titre du formulaire
        return "portees/formulaire";
    }

    // ===========================
    // MARQUER ÉCHEC (fausse gestation, avortement...)
    // ===========================
    @PostMapping("/{id}/echec")
    public String marquerEchec(@PathVariable Long id,
                               @RequestParam(required = false) String motif,
                               RedirectAttributes redirectAttributes) {
        gestationService.marquerEchec(id, motif != null ? motif : "Non précisé");
        redirectAttributes.addFlashAttribute("info", "Gestation marquée comme échec.");
        return "redirect:/gestations";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gestationService.delete(id);
        redirectAttributes.addFlashAttribute("succes", "Gestation supprimée !");
        return "redirect:/gestations";
    }
}

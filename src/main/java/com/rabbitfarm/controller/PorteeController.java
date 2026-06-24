package com.rabbitfarm.controller;

import com.rabbitfarm.model.Lapin;
import com.rabbitfarm.model.Portee;
import com.rabbitfarm.service.LapinService;
import com.rabbitfarm.service.PorteeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/portees")
@RequiredArgsConstructor
public class PorteeController {

    private final PorteeService porteeService;
    private final LapinService lapinService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("portees", porteeService.findAll());
        model.addAttribute("totalNes", porteeService.getTotalNes());
        model.addAttribute("totalDeces", porteeService.getTotalDeces());
        model.addAttribute("nesduMois", porteeService.getNesduMois());
        return "portees/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("portee", new Portee());
        model.addAttribute("lapines", lapinService.findFemellesReproductrices());
        model.addAttribute("statuts", Portee.StatutPortee.values());
        return "portees/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Portee portee,
                        BindingResult result, Model model,
                        RedirectAttributes redirectAttributes) {

        // CORRECTION : Assurer l'association de l'objet Lapin complet avant la validation stricte ou la sauvegarde
        if (portee.getLapine() != null && portee.getLapine().getId() != null) {
            Lapin lapineReal = lapinService.findById(portee.getLapine().getId());
            portee.setLapine(lapineReal);
        }

        if (result.hasErrors()) {
            model.addAttribute("lapines", lapinService.findFemellesReproductrices());
            model.addAttribute("statuts", Portee.StatutPortee.values());
            return "portees/formulaire";
        }

        porteeService.save(portee);
        redirectAttributes.addFlashAttribute("succes", "Portée enregistrée avec succès !");
        return "redirect:/portees";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        model.addAttribute("portee", porteeService.findById(id));
        model.addAttribute("lapines", lapinService.findFemellesReproductrices());
        model.addAttribute("statuts", Portee.StatutPortee.values());
        return "portees/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Portee portee,
                           BindingResult result, Model model,
                           RedirectAttributes redirectAttributes) {

        // CORRECTION : Réassocier l'objet complet également lors de la modification
        if (portee.getLapine() != null && portee.getLapine().getId() != null) {
            Lapin lapineReal = lapinService.findById(portee.getLapine().getId());
            portee.setLapine(lapineReal);
        }

        if (result.hasErrors()) {
            model.addAttribute("lapines", lapinService.findFemellesReproductrices());
            model.addAttribute("statuts", Portee.StatutPortee.values());
            return "portees/formulaire";
        }

        porteeService.update(id, portee);
        redirectAttributes.addFlashAttribute("succes", "Portée modifiée !");
        return "redirect:/portees";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        porteeService.delete(id);
        redirectAttributes.addFlashAttribute("succes", "Portée supprimée !");
        return "redirect:/portees";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowNestedPaths(true);
    }
}
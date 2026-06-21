package com.rabbitfarm.controller;

import com.rabbitfarm.model.Lapin;
import com.rabbitfarm.service.CageService;
import com.rabbitfarm.service.LapinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lapins")
@RequiredArgsConstructor
public class LapinController {

    private final LapinService lapinService;
    private final CageService cageService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("lapins", lapinService.findAll());
        model.addAttribute("totalMales", lapinService.countMalesReproducteurs());
        model.addAttribute("totalFemelles", lapinService.countFemellesReproductrices());
        model.addAttribute("totalEngraissement", lapinService.countEngraissement());
        return "lapins/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("lapin", new Lapin());
        model.addAttribute("cages", cageService.findAll());
        model.addAttribute("sexes", Lapin.Sexe.values());
        model.addAttribute("statuts", Lapin.StatutLapin.values());
        model.addAttribute("types", Lapin.TypeLapin.values());
        return "lapins/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Lapin lapin,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("cages", cageService.findAll());
            model.addAttribute("sexes", Lapin.Sexe.values());
            model.addAttribute("statuts", Lapin.StatutLapin.values());
            model.addAttribute("types", Lapin.TypeLapin.values());
            return "lapins/formulaire";
        }
        lapinService.save(lapin);
        redirectAttributes.addFlashAttribute("succes", "Lapin ajouté avec succès !");
        return "redirect:/lapins";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("lapin", lapinService.findById(id));
        return "lapins/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        model.addAttribute("lapin", lapinService.findById(id));
        model.addAttribute("cages", cageService.findAll());
        model.addAttribute("sexes", Lapin.Sexe.values());
        model.addAttribute("statuts", Lapin.StatutLapin.values());
        model.addAttribute("types", Lapin.TypeLapin.values());
        return "lapins/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Lapin lapin,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("cages", cageService.findAll());
            return "lapins/formulaire";
        }
        lapinService.update(id, lapin);
        redirectAttributes.addFlashAttribute("succes", "Lapin modifié avec succès !");
        return "redirect:/lapins";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        lapinService.delete(id);
        redirectAttributes.addFlashAttribute("succes", "Lapin supprimé !");
        return "redirect:/lapins";
    }
}

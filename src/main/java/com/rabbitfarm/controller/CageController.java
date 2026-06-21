package com.rabbitfarm.controller;

import com.rabbitfarm.model.Cage;
import com.rabbitfarm.service.CageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cages")
@RequiredArgsConstructor
public class CageController {

    private final CageService cageService;


    @GetMapping
    public String liste(Model model) {
        model.addAttribute("cages", cageService.findAll());
        model.addAttribute("totalCages", cageService.countTotal());
        model.addAttribute("cagesLibres", cageService.countLibres());

        return "cages/liste";
    }


    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("cage", new Cage());
        model.addAttribute("types", Cage.TypeCage.values());
        model.addAttribute("statuts", Cage.StatutCage.values());

        return "cages/formulaire";
    }


    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Cage cage,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("types", Cage.TypeCage.values());
            model.addAttribute("statuts", Cage.StatutCage.values());

            return "cages/formulaire";
        }

        cageService.save(cage);

        redirectAttributes.addFlashAttribute(
                "succes",
                "Cage ajoutée !"
        );

        return "redirect:/cages";
    }


    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id,
                                     Model model) {

        model.addAttribute("cage", cageService.findById(id));
        model.addAttribute("types", Cage.TypeCage.values());
        model.addAttribute("statuts", Cage.StatutCage.values());

        return "cages/formulaire";
    }


    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Cage cage,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("types", Cage.TypeCage.values());
            model.addAttribute("statuts", Cage.StatutCage.values());

            return "cages/formulaire";
        }

        cageService.update(id, cage);

        redirectAttributes.addFlashAttribute(
                "succes",
                "Cage modifiée !"
        );

        return "redirect:/cages";
    }


    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {

        cageService.delete(id);

        redirectAttributes.addFlashAttribute(
                "succes",
                "Cage supprimée !"
        );

        return "redirect:/cages";
    }
}
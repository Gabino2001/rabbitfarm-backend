package com.rabbitfarm.controller;

import com.rabbitfarm.model.Vente;
import com.rabbitfarm.service.VenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("ventes", venteService.findAll());
        model.addAttribute("revenusTotaux", venteService.getRevenusTotaux());
        model.addAttribute("revenusMois", venteService.getRevenusDuMois());
        model.addAttribute("poidsMois", venteService.getPoidsDuMois());
        return "ventes/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("vente", new Vente());
        model.addAttribute("types", Vente.TypeVente.values());
        return "ventes/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Vente vente,
                        BindingResult result, Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("types", Vente.TypeVente.values());
            return "ventes/formulaire";
        }
        venteService.save(vente);
        redirectAttributes.addFlashAttribute("succes", "Vente enregistrée !");
        return "redirect:/ventes";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        model.addAttribute("vente", venteService.findById(id));
        model.addAttribute("types", Vente.TypeVente.values());
        return "ventes/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Vente vente,
                           BindingResult result, Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("types", Vente.TypeVente.values());
            return "ventes/formulaire";
        }
        venteService.update(id, vente);
        redirectAttributes.addFlashAttribute("succes", "Vente modifiée !");
        return "redirect:/ventes";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        venteService.delete(id);
        redirectAttributes.addFlashAttribute("succes", "Vente supprimée !");
        return "redirect:/ventes";
    }
}

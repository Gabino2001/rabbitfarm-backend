package com.rabbitfarm.controller;

import com.rabbitfarm.model.Stock;
import com.rabbitfarm.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("stocks", stockService.findAll());
        model.addAttribute("stocksCritiques", stockService.findStocksCritiques());
        model.addAttribute("nbAlertes", stockService.countAlertes());
        return "stocks/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("stock", new Stock());
        return "stocks/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute Stock stock,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "stocks/formulaire";
        stockService.save(stock);
        redirectAttributes.addFlashAttribute("succes", "Stock ajouté !");
        return "redirect:/stocks";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        model.addAttribute("stock", stockService.findById(id));
        return "stocks/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute Stock stock,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "stocks/formulaire";
        stockService.update(id, stock);
        redirectAttributes.addFlashAttribute("succes", "Stock modifié !");
        return "redirect:/stocks";
    }

    @PostMapping("/{id}/ajouter")
    public String ajouterQuantite(@PathVariable Long id,
                                  @RequestParam (name = "quantite", required = false, defaultValue = "0.0")Double quantite,

                                  RedirectAttributes redirectAttributes) {
        stockService.ajouterQuantite(id, quantite);
        redirectAttributes.addFlashAttribute("succes", "Quantité ajoutée !");
        return "redirect:/stocks";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        stockService.delete(id);
        redirectAttributes.addFlashAttribute("succes", "Stock supprimé !");
        return "redirect:/stocks";
    }
}

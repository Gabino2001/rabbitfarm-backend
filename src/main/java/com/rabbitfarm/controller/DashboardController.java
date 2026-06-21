package com.rabbitfarm.controller;

import com.rabbitfarm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final LapinService lapinService;
    private final PorteeService porteeService;
    private final CageService cageService;
    private final StockService stockService;
    private final VenteService venteService;
    private final SevrageService sevrageService; // ✅
    private final GestationService gestationService;
    @GetMapping({ "/dashboard"})
    public String dashboard(Model model) {
        // Stats lapins
        model.addAttribute("totalLapins", lapinService.countTotal());
        model.addAttribute("malesReproducteurs", lapinService.countMalesReproducteurs());
        model.addAttribute("femellesReproductrices", lapinService.countFemellesReproductrices());
        model.addAttribute("lapinsEngraissement", sevrageService.getTotalLapereauEnEngraissement());

        // Stats cages
        model.addAttribute("totalCages", cageService.countTotal());
        model.addAttribute("cagesLibres", cageService.countLibres());

        // Stats portées du mois
        model.addAttribute("nesduMois", porteeService.getNesduMois());
        model.addAttribute("totalDeces", porteeService.getTotalDeces());
        model.addAttribute("sevragesAVenir", porteeService.getSevragesAVenir());

        model.addAttribute("gestationsEnCours", gestationService.countEnCours());
        model.addAttribute("gestationsEnRetard", gestationService.findEnRetard());
        model.addAttribute("gestationsProches", gestationService.findProchesDuTerme());

        // Stats ventes
        model.addAttribute("revenusMois", venteService.getRevenusDuMois());
        model.addAttribute("poidsMois", venteService.getPoidsDuMois());

        // Alertes stocks
        model.addAttribute("stocksCritiques", stockService.findStocksCritiques());
        model.addAttribute("stocksBas", stockService.findStocksBas());
        model.addAttribute("nbAlertes", stockService.countAlertes()
        + gestationService.findEnRetard().size()); // ✅ inclut les gestations en retard
        return "dashboard";
    }
}

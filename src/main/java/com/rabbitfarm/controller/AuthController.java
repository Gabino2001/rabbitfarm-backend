package com.rabbitfarm.controller;

import com.rabbitfarm.model.RegistreDTO;
import com.rabbitfarm.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurService utilisateurService;

    // ===========================
    // PAGE LOGIN
    // ===========================
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String erreur,
            @RequestParam(required = false) String deconnecte,
            @RequestParam(required = false) String expire,
            @RequestParam(required = false) String inscrit,
            Model model) {

        if (erreur != null)      model.addAttribute("erreur", "Email ou mot de passe incorrect.");
        if (deconnecte != null)  model.addAttribute("info", "Vous avez été déconnecté.");
        if (expire != null)      model.addAttribute("info", "Votre session a expiré. Reconnectez-vous.");
        if (inscrit != null)     model.addAttribute("succes", "Compte créé ! Vous pouvez vous connecter.");

        return "auth/login";
    }

    // ===========================
    // PAGE REGISTER - GET
    // ===========================
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registreDTO", new RegistreDTO());
        return "auth/register";
    }

    // ===========================
    // PAGE REGISTER - POST
    // ===========================
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegistreDTO registreDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validation des champs
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Vérification mots de passe
        if (!registreDTO.motDePasseCorrespond()) {
            model.addAttribute("erreurMdp", "Les mots de passe ne correspondent pas.");
            return "auth/register";
        }

        // Inscription
        try {
            utilisateurService.inscrire(registreDTO);
            redirectAttributes.addFlashAttribute("succes", "Compte créé avec succès !");
            return "redirect:/auth/login?inscrit=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            return "auth/register";
        }
    }

}

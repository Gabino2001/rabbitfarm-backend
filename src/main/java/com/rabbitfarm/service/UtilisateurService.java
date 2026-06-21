package com.rabbitfarm.service;

import com.rabbitfarm.model.Utilisateur;
import com.rabbitfarm.model.RegistreDTO;
import com.rabbitfarm.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur inscrire(RegistreDTO dto) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .prenom(dto.getPrenom())
                .nom(dto.getNom())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getMotDePasse()))
                .role(Utilisateur.Role.UTILISATEUR)
                .actif(true)
                .build();

        return utilisateurRepository.save(utilisateur);
    }

    public void mettreAJourDerniereConnexion(String email) {
        utilisateurRepository.findByEmail(email).ifPresent(u -> {
            u.setDerniereConnexion(LocalDateTime.now());
            utilisateurRepository.save(u);
        });
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }
}

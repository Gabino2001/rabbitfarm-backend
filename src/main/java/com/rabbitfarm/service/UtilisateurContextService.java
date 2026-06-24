package com.rabbitfarm.service;

import com.rabbitfarm.model.Utilisateur;
import com.rabbitfarm.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurContextService {

    private final UtilisateurRepository utilisateurRepository;

    public Utilisateur getUtilisateurConnecte() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur connecté introuvable : " + email));
    }
}

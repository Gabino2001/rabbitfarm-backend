package com.rabbitfarm.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistreDTO {

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Adresse email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotBlank(message = "La confirmation est obligatoire")
    private String confirmerMotDePasse;

    // Vérifie que les deux mots de passe correspondent
    public boolean motDePasseCorrespond() {
        return motDePasse != null && motDePasse.equals(confirmerMotDePasse);
    }
}

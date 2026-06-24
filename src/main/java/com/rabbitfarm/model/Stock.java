package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'aliment est obligatoire")
    @Column(nullable = false)
    private String aliment;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double quantite;

    @NotBlank
    private String unite;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double seuilAlerte;

    private String fournisseur;
    private String notes;

    // ✅ Lien vers le propriétaire
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public StatutStock getStatut() {
        if (quantite <= 0) return StatutStock.EPUISE;
        if (quantite <= seuilAlerte * 0.5) return StatutStock.CRITIQUE;
        if (quantite <= seuilAlerte) return StatutStock.BAS;
        return StatutStock.OK;
    }

    public enum StatutStock {
        OK, BAS, CRITIQUE, EPUISE
    }
}

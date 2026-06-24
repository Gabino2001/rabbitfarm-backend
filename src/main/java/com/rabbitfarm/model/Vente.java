package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date de vente est obligatoire")
    private LocalDate dateVente;

    @NotBlank(message = "Le client est obligatoire")
    private String client;

    @NotNull
    @DecimalMin(value = "0.1", message = "Le poids doit être supérieur à 0")
    private Double poidsKg;

    @NotNull
    @DecimalMin(value = "0", message = "Le prix doit être positif")
    private Double prixTotal;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TypeVente type = TypeVente.VIANDE;

    @Min(value = 1)
    private Integer nombreTetes;

    // ✅ Lien vers le propriétaire
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    private String notes;

    public enum TypeVente {
        VIANDE, REPRODUCTEUR, LAPEREAU
    }

    public Double getPrixParKg() {
        if (poidsKg == null || poidsKg == 0) return 0.0;
        return prixTotal / poidsKg;
    }
}

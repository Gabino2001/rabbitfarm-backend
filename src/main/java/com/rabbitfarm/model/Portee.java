package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "portees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lapine_id", nullable = false)
    @NotNull(message = "La lapine est obligatoire")
    @ToString.Exclude
    private Lapin lapine;

    @NotNull(message = "La date de mise bas est obligatoire")
    private LocalDate dateMiseBas;

    @NotNull
    @Min(value = 0, message = "Le nombre de nés doit être positif")
    private Integer nbNes;

    @Min(value = 0)
    @Builder.Default
    private Integer nbVivants = 0;

    @Min(value = 0)
    @Builder.Default
    private Integer nbDeces = 0;

    private LocalDate dateSevrage;
    private Integer nbrSevres;
    private Double poidsMoyenSevrage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutPortee statut = StatutPortee.EN_COURS;

    @Builder.Default
    private boolean sevree = false;

    @OneToOne(mappedBy = "portee", fetch = FetchType.LAZY)
    private LotEngraissement lotEngraissement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestation_id", unique = true)
    private Gestation gestation;

    // ✅ Lien vers le propriétaire
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    private String notes;

    public enum StatutPortee {
        EN_COURS, SEVREE, TERMINEE
    }

    public boolean isSevree() {
        return this.sevree;
    }

    public double getTauxSurvie() {
        if (nbNes == null || nbNes == 0) return 0;
        return ((double) (nbNes - nbDeces) / nbNes) * 100;
    }

    public double getTauxReussiteSevrage() {
        if (nbVivants == null || nbVivants == 0 || nbrSevres == null) return 0;
        return ((double) nbrSevres / nbVivants) * 100;
    }
}

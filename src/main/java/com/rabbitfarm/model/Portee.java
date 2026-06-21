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

    // Nombre de lapereaux effectivement sevrés (peut différer de nbVivants
    // si décès supplémentaires entre naissance et sevrage)
    private Integer nbrSevres;

    // Poids moyen au sevrage (kg), saisi manuellement ou estimé
    private Double poidsMoyenSevrage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutPortee statut = StatutPortee.EN_COURS;

    // Indique si le sevrage a déjà été effectué (lot engraissement créé)
    @Builder.Default
    private boolean sevree = false;

    // Lien vers le lot d'engraissement créé automatiquement au sevrage
    @OneToOne(mappedBy = "portee", fetch = FetchType.LAZY)
    private LotEngraissement lotEngraissement;

    // Lien vers la gestation d'origine (null si portée saisie directement)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestation_id", unique = true)
    private Gestation gestation;

    private String notes;

    public enum StatutPortee {
        EN_COURS, SEVREE, TERMINEE
    }

    // Getter manuel explicite (Lombok @Data en génère un équivalent,
    // gardé ici pour la clarté / au cas où l'IDE traîne à rafraîchir)
    public boolean isSevree() {
        return this.sevree;
    }

    // Taux de survie à la naissance
    public double getTauxSurvie() {
        if (nbNes == null || nbNes == 0) return 0;
        return ((double) (nbNes - nbDeces) / nbNes) * 100;
    }

    // Taux de réussite du sevrage (sevrés / vivants à la naissance)
    public double getTauxReussiteSevrage() {
        if (nbVivants == null || nbVivants == 0 || nbrSevres == null) return 0;
        return ((double) nbrSevres / nbVivants) * 100;
    }
}
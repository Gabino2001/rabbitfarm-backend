package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lapins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lapin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "La race est obligatoire")
    private String race;

    @NotNull(message = "Le sexe est obligatoire")
    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @DecimalMin(value = "0.0", message = "Le poids doit être positif")
    private Double poidKg;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutLapin statut = StatutLapin.ACTIF;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TypeLapin type = TypeLapin.REPRODUCTEUR;

    // CORRECTION : On exclut les proxies des méthodes automatiques de Lombok
    // pour stopper les crashs de conversion ConversionFailedException et les boucles infinies
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cage_id")
    private Cage cage;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "lapine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Portee> portees;

    private String notes;

    // Enums
    public enum Sexe {
        MALE, FEMELLE
    }

    public enum StatutLapin {
        ACTIF, GESTANTE, REPOS, MALADE, DECEDE, VENDU
    }

    public enum TypeLapin {
        REPRODUCTEUR, ENGRAISSEMENT
    }

    // Méthodes utilitaires
    public int getNombrePortees() {
        return portees != null ? portees.size() : 0;
    }

    public int getTotalNes() {
        if (portees == null) return 0;
        return portees.stream().mapToInt(Portee::getNbNes).sum();
    }

    public int getTotalDeces() {
        if (portees == null) return 0;
        return portees.stream().mapToInt(Portee::getNbDeces).sum();
    }
}
package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de cage est obligatoire")
    @Column(unique = true, nullable = false)
    private String numero;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeCage type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutCage statut = StatutCage.LIBRE;

    @Min(value = 1)
    private Integer capaciteMax;

    @OneToMany(mappedBy = "cage", fetch = FetchType.LAZY)
    private List<Lapin> lapins;

    // À ajouter dans votre fichier Cage.java :
    public int getTauxOccupation() {
        if (capaciteMax == null || capaciteMax == 0) {
            return 0;
        }
        return (getNombreOccupants() * 100) / capaciteMax;
    }
    private String localisation;
    private String notes;

    public enum TypeCage {
        REPRODUCTION, MATERNITE, ENGRAISSEMENT, QUARANTAINE
    }

    public enum StatutCage {
        LIBRE, OCCUPEE, MAINTENANCE
    }

    public int getNombreOccupants() {
        return lapins != null ? lapins.size() : 0;
    }

    public boolean isDisponible() {
        return statut == StatutCage.LIBRE;
    }
}

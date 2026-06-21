package com.rabbitfarm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "lots_engraissement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotEngraissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Portée d'origine (liaison automatique au sevrage)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portee_id", unique = true)
    private Portee portee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cage_id")
    private Cage cage;

    // Nombre de lapereaux au départ du lot
    private Integer nombreInitial;

    // Nombre actuel (diminue si décès en engraissement)
    private Integer nombreActuel;

    // Décès pendant l'engraissement
    @Builder.Default
    private Integer nombreDeces = 0;

    private LocalDate dateEntree;  // = date de sevrage
    private LocalDate dateSortie;  // abattage ou vente

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutLot statut = StatutLot.EN_COURS;

    private String notes;

    public enum StatutLot {
        EN_COURS, VENDU, TERMINE
    }

    // Nom affiché automatiquement
    public String getNom() {
        if (portee != null && portee.getLapine() != null) {
            return "Lot " + portee.getLapine().getNom()
                 + " (" + dateEntree + ")";
        }
        return "Lot #" + id;
    }

    // Poids estimé total (basé sur un poids moyen de 2 kg/lapereau)
    public double getPoidsEstimeKg() {
        return nombreActuel != null ? nombreActuel * 2.0 : 0;
    }
}

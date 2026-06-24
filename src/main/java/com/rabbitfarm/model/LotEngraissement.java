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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portee_id", unique = true)
    private Portee portee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cage_id")
    private Cage cage;

    private Integer nombreInitial;
    private Integer nombreActuel;

    @Builder.Default
    private Integer nombreDeces = 0;

    private LocalDate dateEntree;
    private LocalDate dateSortie;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutLot statut = StatutLot.EN_COURS;

    // ✅ Lien vers le propriétaire
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    private String notes;

    public enum StatutLot {
        EN_COURS, VENDU, TERMINE
    }

    public String getNom() {
        if (portee != null && portee.getLapine() != null) {
            return "Lot " + portee.getLapine().getNom()
                 + " (" + dateEntree + ")";
        }
        return "Lot #" + id;
    }

    public double getPoidsEstimeKg() {
        return nombreActuel != null ? nombreActuel * 2.0 : 0;
    }
}

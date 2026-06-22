package com.rabbitfarm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "gestations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lapine_id", nullable = false)
    @NotNull(message = "La lapine est obligatoire")
    private Lapin lapine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_id")
    private Lapin male;

    @NotNull(message = "La date de saillie est obligatoire")
    private LocalDate dateSaillie;

    private LocalDate dateMiseBasPrevue;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutGestation statut = StatutGestation.EN_COURS;

    @OneToOne(mappedBy = "gestation", fetch = FetchType.LAZY)
    private Portee portee;

    private String notes;


    public enum StatutGestation {
        EN_COURS,
        MISE_BAS,
        ECHEC,
        ANNULEE
    }


    public void calculerDatePrevue() {
        if (dateSaillie != null) {
            this.dateMiseBasPrevue = dateSaillie.plusDays(31);
        }
    }


    @PrePersist
    @PreUpdate
    public void avantSauvegarde() {
        calculerDatePrevue();

        if (statut == null) {
            statut = StatutGestation.EN_COURS;
        }
    }


    public long getJoursRestants() {
        if (dateMiseBasPrevue == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dateMiseBasPrevue);
    }


    public boolean isEnRetard() {
        return statut == StatutGestation.EN_COURS
                && dateMiseBasPrevue != null
                && LocalDate.now().isAfter(dateMiseBasPrevue);
    }


    public boolean isProche() {
        if (statut != StatutGestation.EN_COURS || dateMiseBasPrevue == null)
            return false;

        long jours = getJoursRestants();
        return jours >= 0 && jours <= 3;
    }
}
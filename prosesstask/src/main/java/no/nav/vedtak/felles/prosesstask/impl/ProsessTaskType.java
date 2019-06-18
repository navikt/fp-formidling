package no.nav.vedtak.felles.prosesstask.impl;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import no.nav.vedtak.felles.jpa.BaseEntitet;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTypeInfo;

/**
 * Konfigurasjon for en type task
 */
@Entity(name = "ProsessTaskType")
@Table(name = "PROSESS_TASK_TYPE")
public class ProsessTaskType extends BaseEntitet {

    @Column(name = "feil_maks_forsoek", updatable = false, insertable = false, nullable = false)
    private int maksForsøk = 1;

    @Column(name = "feil_sek_mellom_forsoek", updatable = false, insertable = false, nullable = false)
    private int sekundeFørNesteForsøk = 1;

    @ManyToOne
    @JoinColumn(name = "feilhandtering_algoritme", updatable = false, insertable = false)
    private ProsessTaskFeilhand feilhåndteringAlgoritme;

    @Column(name = "cron_expression", nullable = true, updatable = false)
    private String cronExpression;
    @Id
    @Column(name = "kode", updatable = false, insertable = false, nullable = false)
    private String kode;
    @Column(name = "beskrivelse", updatable = false, insertable = false)
    private String beskrivelse;
    /**
     * Navn registrert i databasen.
     */
    @Column(name = "navn", updatable = false, insertable = false)
    private String navn;

    public ProsessTaskType() {
        // Hibernate
        // for hibernate
    }

    public ProsessTaskType(String taskType) {
        Objects.requireNonNull(taskType, "kode");
        this.kode = taskType;
    }

    ProsessTaskType(String taskType, String cronExpression) {
        Objects.requireNonNull(taskType, "kode");
        this.kode = taskType;
        this.cronExpression = cronExpression;
    }

    public int getMaksForsøk() {
        return maksForsøk;
    }

    public int getSekundeFørNesteForsøk() {
        return sekundeFørNesteForsøk;
    }

    public ProsessTaskFeilhand getFeilhåndteringAlgoritme() {
        return feilhåndteringAlgoritme;
    }

    void setFeilhåndteringAlgoritme(ProsessTaskFeilhand feilhåndteringAlgoritme) {
        this.feilhåndteringAlgoritme = feilhåndteringAlgoritme;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    boolean getErGjentagende() {
        return cronExpression != null;
    }

    public ProsessTaskTypeInfo tilProsessTaskTypeInfo() {
        return new ProsessTaskTypeInfo(getKode(), getMaksForsøk());
    }

    public String getKode() {
        return kode;
    }

    public String getNavn() {
        return navn;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ProsessTaskType)) {
            return false;
        }
        ProsessTaskType other = (ProsessTaskType) object;
        return Objects.equals(getKode(), other.getKode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<kode=" + getKode() + ", navn=" + getNavn() + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}

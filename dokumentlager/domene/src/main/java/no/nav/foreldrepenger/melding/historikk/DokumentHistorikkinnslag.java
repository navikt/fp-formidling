package no.nav.foreldrepenger.melding.historikk;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.dokumentdata.BaseEntitet;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@Entity(name = "DokumentHistorikkinnslag")
@Table(name = "DOKUMENT_HISTORIKKINNSLAG")
public class DokumentHistorikkinnslag extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HISTORIKK")
    private Long id;

    @Column(name = "behandling_id")
    private Long behandlingId;

    @Column(name = "dokument_id")
    private String dokumentId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "journalpostId", column = @Column(name = "journalpost_id")))
    private JournalpostId journalpostId;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "historikk_aktoer", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + HistorikkAktør.DISCRIMINATOR + "'"))
    private HistorikkAktør historikkAktør;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "historikk_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + HistorikkinnslagType.DISCRIMINATOR + "'"))
    private HistorikkinnslagType historikkinnslagType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dokument_mal_navn", nullable = false)
    private DokumentMalType dokumentMalType;

    private DokumentHistorikkinnslag() {
    }

    DokumentHistorikkinnslag(long behandlingId, String dokumentId, JournalpostId journalpostId, DokumentMalType dokumentMalType, HistorikkinnslagType historikkinnslagType) {
        this.behandlingId = behandlingId;
        this.dokumentId = dokumentId;
        this.journalpostId = journalpostId;
        this.dokumentMalType = dokumentMalType;
        this.historikkinnslagType = historikkinnslagType;
        this.dokumentMalType = dokumentMalType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getDokumentId() {
        return dokumentId;
    }

    public JournalpostId getJournalpostId() {
        return journalpostId;
    }

    public HistorikkinnslagType getHistorikkinnslagType() {
        return historikkinnslagType;
    }

    public HistorikkAktør getHistorikkAktør() {
        return historikkAktør;
    }

    public DokumentMalType getDokumentMalType() {
        return dokumentMalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHistorikkinnslag that = (DokumentHistorikkinnslag) o;
        return behandlingId.equals(that.behandlingId) &&
                dokumentId.equals(that.dokumentId) &&
                journalpostId.equals(that.journalpostId) &&
                Objects.equals(historikkAktør, that.historikkAktør) &&
                Objects.equals(historikkinnslagType, that.historikkinnslagType) &&
                dokumentMalType.equals(that.dokumentMalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, dokumentId, journalpostId, historikkAktør, historikkinnslagType, dokumentMalType);
    }

    @Override
    public String toString() {
        return "DokumentHistorikkinnslag{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", dokumentId='" + dokumentId + '\'' +
                ", journalpostId=" + journalpostId +
                ", historikkAktør=" + historikkAktør +
                ", historikkinnslagType=" + historikkinnslagType +
                ", dokumentMalType=" + dokumentMalType +
                '}';
    }

    public static class Builder {
        private Long behandlingId;
        private String dokumentId;
        private JournalpostId journalpostId;
        private HistorikkAktør historikkAktør;
        private HistorikkinnslagType historikkinnslagType;
        private DokumentMalType dokumentMalType;

        public DokumentHistorikkinnslag.Builder medBehandlingId(long behandlingId) {
            this.behandlingId = behandlingId;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medDokumentId(String dokumentId) {
            this.dokumentId = dokumentId;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medJournalpostId(JournalpostId journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medHistorikkAktør(HistorikkAktør historikkAktør) {
            this.historikkAktør = historikkAktør;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medHistorikkinnslagType(HistorikkinnslagType historikkinnslagType) {
            this.historikkinnslagType = historikkinnslagType;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentHistorikkinnslag build() {
            verifyStateForBuild();
            DokumentHistorikkinnslag dokumentHistorikkinnslag = new DokumentHistorikkinnslag(behandlingId, dokumentId, journalpostId, dokumentMalType, historikkinnslagType);
            dokumentHistorikkinnslag.historikkAktør = historikkAktør;
            return dokumentHistorikkinnslag;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(dokumentId, "dokumentId");
            Objects.requireNonNull(journalpostId, "journalpostId");
            Objects.requireNonNull(behandlingId, "behandlingId");
            Objects.requireNonNull(dokumentMalType, "dokumentMalType");
            Objects.requireNonNull(historikkinnslagType, "historikkinnslagType");

        }

    }

}

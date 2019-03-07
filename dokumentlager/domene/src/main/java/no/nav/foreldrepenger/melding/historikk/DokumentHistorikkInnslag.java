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
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@Entity(name = "DokumentHistorikkInnslag")
@Table(name = "DOKUMENT_HISTORIKK_INNSLAG")
public class DokumentHistorikkInnslag extends BaseEntitet {
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

    private DokumentHistorikkInnslag() {
    }

    DokumentHistorikkInnslag(long behandlingId, String dokumentId, JournalpostId journalpostId) {
        this.behandlingId = behandlingId;
        this.dokumentId = dokumentId;
        this.journalpostId = journalpostId;
    }

    @Override
    public String toString() {
        return "DokumentHistorikkInnslag{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", dokumentId='" + dokumentId + '\'' +
                ", journalpostId=" + journalpostId +
                ", historikkAktør=" + historikkAktør +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHistorikkInnslag that = (DokumentHistorikkInnslag) o;
        return behandlingId.equals(that.behandlingId) &&
                dokumentId.equals(that.dokumentId) &&
                journalpostId.equals(that.journalpostId) &&
                Objects.equals(historikkAktør, that.historikkAktør);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, dokumentId, journalpostId, historikkAktør);
    }

    public static class Builder {
        private Long behandlingId;
        private String dokumentId;
        private JournalpostId journalpostId;
        private HistorikkAktør historikkAktør;

        public DokumentHistorikkInnslag.Builder medBehandlingId(long behandlingId) {
            this.behandlingId = behandlingId;
            return this;
        }

        public DokumentHistorikkInnslag.Builder medDokumentId(String dokumentId) {
            this.dokumentId = dokumentId;
            return this;
        }

        public DokumentHistorikkInnslag.Builder medJournalpostId(JournalpostId journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        public DokumentHistorikkInnslag.Builder medHistorikkAktør(HistorikkAktør historikkAktør) {
            this.historikkAktør = historikkAktør;
            return this;
        }

        public DokumentHistorikkInnslag build() {
            verifyStateForBuild();
            DokumentHistorikkInnslag dokumentHistorikkInnslag = new DokumentHistorikkInnslag(behandlingId, dokumentId, journalpostId);
            dokumentHistorikkInnslag.historikkAktør = historikkAktør;
            return dokumentHistorikkInnslag;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(dokumentId, "dokumentId");
            Objects.requireNonNull(journalpostId, "journalpostId");
            Objects.requireNonNull(behandlingId, "behandlingId");
        }

    }

}

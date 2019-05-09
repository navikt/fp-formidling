package no.nav.foreldrepenger.melding.historikk;

import java.util.Objects;
import java.util.UUID;

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

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.jpa.BaseEntitet;

@Entity(name = "DokumentHistorikkinnslag")
@Table(name = "DOKUMENT_HISTORIKKINNSLAG")
public class DokumentHistorikkinnslag extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HISTORIKK")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @Column(name = "hendelse_id")
    private Long hendelseId;

    @Column(name = "dokument_id")
    private String dokumentId;

    @Embedded
    private JournalpostId journalpostId;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "historikk_aktoer", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + HistorikkAktør.DISCRIMINATOR + "'"))
    private HistorikkAktør historikkAktør;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "historikkinnslag_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + HistorikkinnslagType.DISCRIMINATOR + "'"))
    private HistorikkinnslagType historikkinnslagType;

    @ManyToOne
    @JoinColumn(name = "dokument_mal_navn", nullable = false)
    private DokumentMalType dokumentMalType;

    @Column
    private String xml;

    public DokumentHistorikkinnslag() {
        //Hibernate
    }

    DokumentHistorikkinnslag(UUID behandlingUuid, long hendelseId, String dokumentId, JournalpostId journalpostId, DokumentMalType dokumentMalType, HistorikkinnslagType historikkinnslagType, String xml) {
        this.behandlingUuid = behandlingUuid;
        this.hendelseId = hendelseId;
        this.dokumentId = dokumentId;
        this.journalpostId = journalpostId;
        this.dokumentMalType = dokumentMalType;
        this.historikkinnslagType = historikkinnslagType;
        this.dokumentMalType = dokumentMalType;
        this.xml = xml;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHistorikkinnslag that = (DokumentHistorikkinnslag) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(behandlingUuid, that.behandlingUuid) &&
                Objects.equals(hendelseId, that.hendelseId) &&
                Objects.equals(dokumentId, that.dokumentId) &&
                Objects.equals(journalpostId, that.journalpostId) &&
                Objects.equals(historikkAktør, that.historikkAktør) &&
                Objects.equals(historikkinnslagType, that.historikkinnslagType) &&
                Objects.equals(dokumentMalType, that.dokumentMalType) &&
                Objects.equals(xml, that.xml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingUuid, hendelseId, dokumentId, journalpostId, historikkAktør, historikkinnslagType, dokumentMalType, xml);
    }

    @Override
    public String toString() {
        return "DokumentHistorikkinnslag{" +
                "id=" + id +
                ", behandlingId=" + behandlingUuid +
                ", hendelseId=" + hendelseId +
                ", dokumentId='" + dokumentId + '\'' +
                ", journalpostId=" + journalpostId +
                ", historikkAktør=" + historikkAktør +
                ", historikkinnslagType=" + historikkinnslagType +
                ", dokumentMalType=" + dokumentMalType +
                ", xml='" + xml + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public Long getHendelseId() {
        return hendelseId;
    }

    public String getXml() {
        return xml;
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

    public static class Builder {
        private UUID behandlingUuid;
        private Long hendelseId;
        private String dokumentId;
        private JournalpostId journalpostId;
        private HistorikkAktør historikkAktør;
        private HistorikkinnslagType historikkinnslagType;
        private DokumentMalType dokumentMalType;
        private String xml;

        public DokumentHistorikkinnslag.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medHendelseId(long hendelseId) {
            this.hendelseId = hendelseId;
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

        public DokumentHistorikkinnslag.Builder medXml(String xml) {
            this.xml = xml;
            return this;
        }

        public DokumentHistorikkinnslag build() {
            verifyStateForBuild();
            DokumentHistorikkinnslag dokumentHistorikkinnslag = new DokumentHistorikkinnslag(behandlingUuid, hendelseId, dokumentId, journalpostId, dokumentMalType, historikkinnslagType, xml);
            dokumentHistorikkinnslag.historikkAktør = historikkAktør;
            return dokumentHistorikkinnslag;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(hendelseId, "hendelseId");
            Objects.requireNonNull(dokumentId, "dokumentId");
            Objects.requireNonNull(journalpostId, "journalpostId");
            Objects.requireNonNull(dokumentMalType, "dokumentMalType");
            Objects.requireNonNull(historikkinnslagType, "historikkinnslagType");
            Objects.requireNonNull(xml, "xml");
        }
    }
}

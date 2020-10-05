package no.nav.foreldrepenger.melding.historikk;

import no.nav.foreldrepenger.melding.jpa.BaseEntitet;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "DokumentHistorikkinnslag")
@Table(name = "DOKUMENT_HISTORIKKINNSLAG")
public class DokumentHistorikkinnslag extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HISTORIKK")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @Column(name = "historikk_uuid")
    private UUID historikkUuuid;

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

    @Convert(converter = DokumentMalType.KodeverdiConverter.class)
    @Column(name = "dokument_mal_navn", nullable = false)
    private DokumentMalType dokumentMalType = DokumentMalType.UDEFINERT;

    public DokumentHistorikkinnslag() {
        //Hibernate
    }

    DokumentHistorikkinnslag(UUID behandlingUuid, UUID historikkUuuid, long hendelseId, String dokumentId,
                             JournalpostId journalpostId, DokumentMalType dokumentMalType,
                             HistorikkinnslagType historikkinnslagType) {
        this.behandlingUuid = behandlingUuid;
        this.historikkUuuid = historikkUuuid;
        this.hendelseId = hendelseId;
        this.dokumentId = dokumentId;
        this.journalpostId = journalpostId;
        this.dokumentMalType = dokumentMalType;
        this.historikkinnslagType = historikkinnslagType;
        this.dokumentMalType = dokumentMalType;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHistorikkinnslag that = (DokumentHistorikkinnslag) o;
        return Objects.equals(behandlingUuid, that.behandlingUuid) &&
                Objects.equals(hendelseId, that.hendelseId) &&
                Objects.equals(dokumentId, that.dokumentId) &&
                Objects.equals(journalpostId, that.journalpostId) &&
                Objects.equals(historikkAktør, that.historikkAktør) &&
                Objects.equals(historikkinnslagType, that.historikkinnslagType) &&
                Objects.equals(dokumentMalType, that.dokumentMalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingUuid, hendelseId, dokumentId, journalpostId, historikkAktør, historikkinnslagType, dokumentMalType);
    }

    @Override
    public String toString() {
        return "DokumentHistorikkinnslag{" +
                "id=" + id +
                ", behandlingUuid=" + behandlingUuid +
                ", historikkUuuid=" + historikkUuuid +
                ", hendelseId=" + hendelseId +
                ", dokumentId='" + dokumentId + '\'' +
                ", journalpostId=" + journalpostId +
                ", historikkAktør=" + historikkAktør +
                ", historikkinnslagType=" + historikkinnslagType +
                ", dokumentMalType=" + dokumentMalType +
                '}';
    }

    public Long getId() {
        return id;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public UUID getHistorikkUuuid() {
        return historikkUuuid;
    }

    public Long getHendelseId() {
        return hendelseId;
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
        private UUID historikkUuuid;
        private Long hendelseId;
        private String dokumentId;
        private JournalpostId journalpostId;
        private HistorikkAktør historikkAktør;
        private HistorikkinnslagType historikkinnslagType;
        private DokumentMalType dokumentMalType;

        public DokumentHistorikkinnslag.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHistorikkinnslag.Builder medHistorikkUuid(UUID historikkUuuid) {
            this.historikkUuuid = historikkUuuid;
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

        public DokumentHistorikkinnslag build() {
            verifyStateForBuild();
            DokumentHistorikkinnslag dokumentHistorikkinnslag = new DokumentHistorikkinnslag(behandlingUuid, historikkUuuid, hendelseId,
                    dokumentId, journalpostId, dokumentMalType, historikkinnslagType);
            dokumentHistorikkinnslag.historikkAktør = historikkAktør;
            return dokumentHistorikkinnslag;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(historikkUuuid, "historikkUuuid");
            Objects.requireNonNull(hendelseId, "hendelseId");
            Objects.requireNonNull(dokumentId, "dokumentId");
            Objects.requireNonNull(journalpostId, "journalpostId");
            Objects.requireNonNull(dokumentMalType, "dokumentMalType");
            Objects.requireNonNull(historikkinnslagType, "historikkinnslagType");
        }
    }
}

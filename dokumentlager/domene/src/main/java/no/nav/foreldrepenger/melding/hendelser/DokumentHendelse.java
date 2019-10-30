package no.nav.foreldrepenger.melding.hendelser;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.jpa.BaseEntitet;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

@Entity(name = "DokumentHendelse")
@Table(name = "DOKUMENT_HENDELSE")
public class DokumentHendelse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HENDELSE")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @Column(name = "bestilling_uuid")
    private UUID bestillingUuid;

    @ManyToOne
    @JoinColumn(name = "dokument_mal_navn")
    private DokumentMalType dokumentMalType;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "ytelse_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + FagsakYtelseType.DISCRIMINATOR + "'"))
    private FagsakYtelseType ytelseType;

    @Column(name = "gjelder_vedtak")
    private Boolean gjelderVedtak;

    @Column
    private String tittel;

    @Column
    private String fritekst;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "historikk_aktoer", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + HistorikkAktør.DISCRIMINATOR + "'"))
    private HistorikkAktør historikkAktør = HistorikkAktør.UDEFINERT;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "revurdering_varsling_arsak", referencedColumnName = "kode"))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + RevurderingVarslingÅrsak.DISCRIMINATOR + "'"))
    private RevurderingVarslingÅrsak revurderingVarslingÅrsak;

    @Column(name = "behandlende_enhet_navn")
    private String behandlendeEnhetNavn;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "vedtaksbrev", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + Vedtaksbrev.DISCRIMINATOR + "'"))
    private Vedtaksbrev vedtaksbrev = Vedtaksbrev.UDEFINERT;

    //Brukes KUN til forhåndsvisning
    @Transient
    private boolean erOpphevetKlage;

    public DokumentHendelse() {
        //For Hibernate
    }

    DokumentHendelse(UUID behandlingUuid, FagsakYtelseType ytelseType) {
        this.behandlingUuid = behandlingUuid;
        this.ytelseType = ytelseType;
    }

    public static DokumentHendelse.Builder builder() {
        return new DokumentHendelse.Builder();
    }

    public Long getId() {
        return id;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public UUID getBestillingUuid() {
        return bestillingUuid;
    }

    public DokumentMalType getDokumentMalType() {
        return dokumentMalType;
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    public Boolean isGjelderVedtak() {
        return gjelderVedtak;
    }

    public String getTittel() {
        return tittel;
    }

    public String getFritekst() {
        return fritekst;
    }

    public Boolean getGjelderVedtak() {
        return gjelderVedtak;
    }

    public HistorikkAktør getHistorikkAktør() {
        return historikkAktør;
    }

    public RevurderingVarslingÅrsak getRevurderingVarslingÅrsak() {
        return revurderingVarslingÅrsak;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public Vedtaksbrev getVedtaksbrev() {
        return vedtaksbrev;
    }

    public boolean getErOpphevetKlage() {
        return erOpphevetKlage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHendelse that = (DokumentHendelse) o;
        return behandlingUuid.equals(that.behandlingUuid) &&
                Objects.equals(dokumentMalType, that.dokumentMalType) &&
                Objects.equals(ytelseType, that.ytelseType) &&
                Objects.equals(gjelderVedtak, that.gjelderVedtak) &&
                Objects.equals(tittel, that.tittel) &&
                Objects.equals(fritekst, that.fritekst) &&
                Objects.equals(historikkAktør, that.historikkAktør) &&
                Objects.equals(behandlendeEnhetNavn, that.behandlendeEnhetNavn) &&
                Objects.equals(vedtaksbrev, that.vedtaksbrev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingUuid, dokumentMalType, ytelseType, gjelderVedtak, tittel, fritekst, historikkAktør,
                behandlendeEnhetNavn, vedtaksbrev);
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" +
                "id=" + id +
                ", behandlingUuid=" + behandlingUuid +
                ", bestillingUuid=" + bestillingUuid +
                ", dokumentMalType=" + dokumentMalType +
                ", ytelseType=" + ytelseType +
                ", gjelderVedtak=" + gjelderVedtak +
                ", tittel='" + tittel + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", historikkAktør=" + historikkAktør +
                ", revurderingVarslingÅrsak=" + revurderingVarslingÅrsak +
                ", behandlendeEnhetNavn=" + behandlendeEnhetNavn +
                ", vedtaksbrev=" + vedtaksbrev +
                ", erOpphevetKlage=" + erOpphevetKlage +
                '}';
    }

    public static class Builder {
        private DokumentMalType dokumentMalType;
        private UUID behandlingUuid;
        private UUID bestillingUuid;
        private FagsakYtelseType ytelseType;
        private String tittel;
        private String fritekst;
        private Boolean gjelderVedtak;
        private HistorikkAktør historikkAktør;
        private RevurderingVarslingÅrsak revurderingVarslingÅrsak;
        private String behandlendeEnhetNavn;
        private Vedtaksbrev vedtaksbrev = Vedtaksbrev.UDEFINERT;
        private boolean erOpphevetKlage;

        public DokumentHendelse.Builder medErOpphevetKlage(boolean erOpphevetKlage) {
            this.erOpphevetKlage = erOpphevetKlage;
            return this;
        }

        public DokumentHendelse.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentHendelse.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHendelse.Builder medBestillingUuid(UUID bestillingUuid) {
            this.bestillingUuid = bestillingUuid;
            return this;
        }

        public DokumentHendelse.Builder medYtelseType(FagsakYtelseType ytelseType) {
            this.ytelseType = ytelseType;
            return this;
        }

        public DokumentHendelse.Builder medTittel(String tittel) {
            this.tittel = tittel;
            return this;
        }

        public DokumentHendelse.Builder medFritekst(String fritekst) {
            this.fritekst = fritekst;
            return this;
        }

        public DokumentHendelse.Builder medGjelderVedtak(Boolean gjelderVedtak) {
            this.gjelderVedtak = gjelderVedtak;
            return this;
        }

        public DokumentHendelse.Builder medHistorikkAktør(HistorikkAktør historikkAktør) {
            this.historikkAktør = historikkAktør;
            return this;
        }

        public DokumentHendelse.Builder medRevurderingVarslingÅrsak(RevurderingVarslingÅrsak revurderingVarslingÅrsak) {
            this.revurderingVarslingÅrsak = revurderingVarslingÅrsak;
            return this;
        }

        public DokumentHendelse.Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            this.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public DokumentHendelse.Builder medVedtaksbrev(Vedtaksbrev vedtaksbrev) {
            this.vedtaksbrev = vedtaksbrev;
            return this;
        }

        public DokumentHendelse build() {
            verifyStateForBuild();
            DokumentHendelse dokumentHendelse = new DokumentHendelse(behandlingUuid, ytelseType);
            dokumentHendelse.bestillingUuid = bestillingUuid;
            dokumentHendelse.erOpphevetKlage = erOpphevetKlage;
            dokumentHendelse.gjelderVedtak = gjelderVedtak;
            dokumentHendelse.fritekst = fritekst;
            dokumentHendelse.tittel = tittel;
            dokumentHendelse.dokumentMalType = dokumentMalType;
            dokumentHendelse.historikkAktør = historikkAktør;
            dokumentHendelse.revurderingVarslingÅrsak = revurderingVarslingÅrsak;
            dokumentHendelse.behandlendeEnhetNavn = behandlendeEnhetNavn;
            dokumentHendelse.vedtaksbrev = vedtaksbrev;
            return dokumentHendelse;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(bestillingUuid, "bestillingUuid");
            Objects.requireNonNull(ytelseType, "ytelseType");
        }

    }
}

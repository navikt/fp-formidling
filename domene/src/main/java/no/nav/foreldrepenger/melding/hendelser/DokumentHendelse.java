package no.nav.foreldrepenger.melding.hendelser;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.jpa.BaseEntitet;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
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

    @Convert(converter = DokumentMalType.KodeverdiConverter.class)
    @Column(name = "dokument_mal_navn")
    private DokumentMalType dokumentMalType;

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "ytelse_type", nullable = false)
    private FagsakYtelseType ytelseType;

    @Column(name = "gjelder_vedtak")
    private Boolean gjelderVedtak;

    @Column
    private String tittel;

    @Column
    private String fritekst;

    @Convert(converter = HistorikkAktør.KodeverdiConverter.class)
    @Column(name = "historikk_aktoer", nullable = false)
    private HistorikkAktør historikkAktør = HistorikkAktør.UDEFINERT;  // TODO bør det ikke stå VL som default???

    @Convert(converter = RevurderingVarslingÅrsak.KodeverdiConverter.class)
    @Column(name = "revurdering_varsling_arsak", nullable = false)
    private RevurderingVarslingÅrsak revurderingVarslingÅrsak = RevurderingVarslingÅrsak.UDEFINERT;

    @Column(name = "behandlende_enhet_navn")
    private String behandlendeEnhetNavn;

    @Convert(converter = Vedtaksbrev.KodeverdiConverter.class)
    @Column(name = "vedtaksbrev", nullable = false)
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

    public HistorikkAktør getHistorikkAktør() {
        return historikkAktør;
    }

    public RevurderingVarslingÅrsak getRevurderingVarslingÅrsak() {
        return revurderingVarslingÅrsak;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public boolean behandlesAvKlageinstans() {
        return behandlendeEnhetNavn != null && behandlendeEnhetNavn.startsWith("NAV Klageinstans");
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
        private DokumentHendelse kladd;

        Builder() {
            this.kladd = new DokumentHendelse();
        }

        public DokumentHendelse.Builder medErOpphevetKlage(boolean erOpphevetKlage) {
            this.kladd.erOpphevetKlage = erOpphevetKlage;
            return this;
        }

        public DokumentHendelse.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.kladd.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentHendelse.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.kladd.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHendelse.Builder medBestillingUuid(UUID bestillingUuid) {
            this.kladd.bestillingUuid = bestillingUuid;
            return this;
        }

        public DokumentHendelse.Builder medYtelseType(FagsakYtelseType ytelseType) {
            this.kladd.ytelseType = ytelseType;
            return this;
        }

        public DokumentHendelse.Builder medTittel(String tittel) {
            this.kladd.tittel = tittel;
            return this;
        }

        public DokumentHendelse.Builder medFritekst(String fritekst) {
            this.kladd.fritekst = fritekst;
            return this;
        }

        public DokumentHendelse.Builder medGjelderVedtak(Boolean gjelderVedtak) {
            this.kladd.gjelderVedtak = gjelderVedtak;
            return this;
        }

        public DokumentHendelse.Builder medHistorikkAktør(HistorikkAktør historikkAktør) {
            this.kladd.historikkAktør = historikkAktør;
            return this;
        }

        public DokumentHendelse.Builder medRevurderingVarslingÅrsak(RevurderingVarslingÅrsak revurderingVarslingÅrsak) {
            this.kladd.revurderingVarslingÅrsak = revurderingVarslingÅrsak;
            return this;
        }

        public DokumentHendelse.Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            this.kladd.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public DokumentHendelse.Builder medVedtaksbrev(Vedtaksbrev vedtaksbrev) {
            this.kladd.vedtaksbrev = vedtaksbrev;
            return this;
        }

        public DokumentHendelse build() {
            verifyStateForBuild();
            return kladd;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(kladd.behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(kladd.bestillingUuid, "bestillingUuid");
            Objects.requireNonNull(kladd.ytelseType, "ytelseType");
        }

    }

    // TEST USE ONLY
    public void setId(Long id) {
        this.id = id;
    }
}

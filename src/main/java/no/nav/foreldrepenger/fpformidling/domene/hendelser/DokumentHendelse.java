package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import jakarta.persistence.*;
import no.nav.foreldrepenger.fpformidling.domene.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.fpformidling.felles.BaseEntitet;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak;

import java.util.Objects;
import java.util.UUID;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "dokument_mal")
    private DokumentMal dokumentMal;

    @Enumerated(EnumType.STRING)
    @Column(name = "journalfoer_som")
    private DokumentMal journalførSom;

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "ytelse_type")
    private FagsakYtelseType ytelseType;

    @Column(name = "gjelder_vedtak")
    private Boolean gjelderVedtak;

    @Column
    private String tittel;

    @Column
    private String fritekst;

    @Convert(converter = RevurderingVarslingÅrsak.KodeverdiConverter.class)
    @Column(name = "revurdering_varsling_arsak", nullable = false)
    private RevurderingVarslingÅrsak revurderingVarslingÅrsak = RevurderingVarslingÅrsak.UDEFINERT;

    @Enumerated(EnumType.STRING)
    @Column(name = "revurdering_aarsak")
    private RevurderingÅrsak revurderingÅrsak;

    @Convert(converter = Vedtaksbrev.KodeverdiConverter.class)
    @Column(name = "vedtaksbrev", nullable = false)
    private Vedtaksbrev vedtaksbrev = Vedtaksbrev.UDEFINERT;

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

    public RevurderingVarslingÅrsak getRevurderingVarslingÅrsak() {
        return revurderingVarslingÅrsak;
    }

    public Vedtaksbrev getVedtaksbrev() {
        return vedtaksbrev;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingUuid, bestillingUuid, dokumentMalType, dokumentMal, journalførSom, ytelseType, gjelderVedtak, tittel, fritekst, revurderingVarslingÅrsak, revurderingÅrsak, vedtaksbrev);
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" + "id=" + id + ", behandlingUuid=" + behandlingUuid + ", bestillingUuid=" + bestillingUuid + ", dokumentMalType="
                + dokumentMal + ", ytelseType=" + ytelseType + ", gjelderVedtak=" + gjelderVedtak + ", tittel='" + tittel + '\'' + ", fritekst='" + (
                fritekst != null ? "****** fritekst ***** " : "null") + '\'' + ", revurderingÅrsak=" + revurderingÅrsak
                + ", vedtaksbrev=" + vedtaksbrev + ", journalførSom=" + journalførSom +'}';
    }


    public static class Builder {
        private DokumentHendelse kladd;

        Builder() {
            this.kladd = new DokumentHendelse();
        }

        public DokumentHendelse.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.kladd.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentHendelse.Builder medDokumentMal(DokumentMal dokumentMal) {
            this.kladd.dokumentMal = dokumentMal;
            return this;
        }

        public DokumentHendelse.Builder medJournalførSom(DokumentMal medJournalførSom) {
            this.kladd.journalførSom = medJournalførSom;
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

        public DokumentHendelse.Builder medRevurderingVarslingÅrsak(RevurderingVarslingÅrsak revurderingVarslingÅrsak) {
            this.kladd.revurderingVarslingÅrsak = revurderingVarslingÅrsak;
            return this;
        }

        public DokumentHendelse.Builder medRevurderingÅrsak(RevurderingÅrsak revurderingÅrsak) {
            this.kladd.revurderingÅrsak = revurderingÅrsak;
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
        }

    }

    // TEST USE ONLY
    public void setId(Long id) {
        this.id = id;
    }
}

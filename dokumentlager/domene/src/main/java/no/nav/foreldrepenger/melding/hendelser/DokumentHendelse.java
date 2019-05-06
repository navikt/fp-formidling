package no.nav.foreldrepenger.melding.hendelser;

import java.util.Objects;

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
import no.nav.foreldrepenger.melding.dokumentdata.BaseEntitet;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;

@Entity(name = "DokumentHendelse")
@Table(name = "DOKUMENT_HENDELSE")
public class DokumentHendelse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HENDELSE")
    private Long id;

    @Column(name = "behandling_id")
    private Long behandlingId;

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

    //Brukes KUN til forhåndsvisning
    @Transient
    private boolean erOpphevetKlage;

    public DokumentHendelse() {
        //For Hibernate
    }

    DokumentHendelse(Long behandlingId, FagsakYtelseType ytelseType) {
        this.behandlingId = behandlingId;
        this.ytelseType = ytelseType;
    }

    public static DokumentHendelse.Builder builder() {
        return new DokumentHendelse.Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getBehandlingId() {
        return behandlingId;
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

    public boolean getErOpphevetKlage() {
        return erOpphevetKlage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHendelse that = (DokumentHendelse) o;
        return id.equals(that.id) &&
                behandlingId.equals(that.behandlingId) &&
                Objects.equals(dokumentMalType, that.dokumentMalType) &&
                Objects.equals(ytelseType, that.ytelseType) &&
                Objects.equals(gjelderVedtak, that.gjelderVedtak) &&
                Objects.equals(tittel, that.tittel) &&
                Objects.equals(fritekst, that.fritekst) &&
                Objects.equals(historikkAktør, that.historikkAktør);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingId, dokumentMalType, ytelseType, gjelderVedtak, tittel, fritekst, historikkAktør);
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", dokumentMalType=" + dokumentMalType +
                ", ytelseType=" + ytelseType +
                ", gjelderVedtak=" + gjelderVedtak +
                ", tittel='" + tittel + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", historikkAktør=" + historikkAktør +
                '}';
    }

    public static class Builder {
        private DokumentMalType dokumentMalType;
        private Long behandlingId;
        private FagsakYtelseType ytelseType;
        private String tittel;
        private String fritekst;
        private Boolean gjelderVedtak;
        private HistorikkAktør historikkAktør;
        private RevurderingVarslingÅrsak revurderingVarslingÅrsak;
        private boolean erOpphevetKlage;

        public DokumentHendelse.Builder medErOpphevetKlage(boolean erOpphevetKlage) {
            this.erOpphevetKlage = erOpphevetKlage;
            return this;
        }

        public DokumentHendelse.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentHendelse.Builder medBehandlingId(Long behandlingId) {
            this.behandlingId = behandlingId;
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

        public DokumentHendelse build() {
            verifyStateForBuild();
            DokumentHendelse dokumentHendelse = new DokumentHendelse(behandlingId, ytelseType);
            dokumentHendelse.erOpphevetKlage = erOpphevetKlage;
            dokumentHendelse.gjelderVedtak = gjelderVedtak;
            dokumentHendelse.fritekst = fritekst;
            dokumentHendelse.tittel = tittel;
            dokumentHendelse.dokumentMalType = dokumentMalType;
            dokumentHendelse.historikkAktør = historikkAktør;
            dokumentHendelse.revurderingVarslingÅrsak = revurderingVarslingÅrsak;
            return dokumentHendelse;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingId, "behandlingId");
            Objects.requireNonNull(ytelseType, "ytelseType");
        }

    }
}

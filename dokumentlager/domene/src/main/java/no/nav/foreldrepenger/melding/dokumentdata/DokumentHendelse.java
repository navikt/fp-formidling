package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.vedtak.felles.jpa.BaseEntitet;

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
    @JoinColumnOrFormula(column = @JoinColumn(name = "behandling_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + BehandlingType.DISCRIMINATOR + "'"))
    private BehandlingType behandlingType;

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
    private HistorikkAktør historikkAktør;

    DokumentHendelse(Long behandlingId) {
        this.behandlingId = behandlingId;
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

    private void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public DokumentMalType getDokumentMalType() {
        return dokumentMalType;
    }

    private void setDokumentMalType(DokumentMalType dokumentMalType) {
        this.dokumentMalType = dokumentMalType;
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    private void setYtelseType(FagsakYtelseType ytelseType) {
        this.ytelseType = ytelseType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    private void setBehandlingType(BehandlingType behandlingType) {
        this.behandlingType = behandlingType;
    }

    public Boolean isGjelderVedtak() {
        return gjelderVedtak;
    }

    public String getTittel() {
        return tittel;
    }

    private void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getFritekst() {
        return fritekst;
    }

    private void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public Boolean getGjelderVedtak() {
        return gjelderVedtak;
    }

    private void setGjelderVedtak(Boolean gjelderVedtak) {
        this.gjelderVedtak = gjelderVedtak;
    }

    public HistorikkAktør getHistorikkAktør() {
        return historikkAktør;
    }

    private void setHistorikkAktør(HistorikkAktør historikkAktør) {
        this.historikkAktør = historikkAktør;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHendelse that = (DokumentHendelse) o;
        return id.equals(that.id) &&
                behandlingId.equals(that.behandlingId) &&
                Objects.equals(dokumentMalType, that.dokumentMalType) &&
                Objects.equals(behandlingType, that.behandlingType) &&
                Objects.equals(ytelseType, that.ytelseType) &&
                Objects.equals(gjelderVedtak, that.gjelderVedtak) &&
                Objects.equals(tittel, that.tittel) &&
                Objects.equals(fritekst, that.fritekst) &&
                Objects.equals(historikkAktør, that.historikkAktør);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingId, dokumentMalType, behandlingType, ytelseType, gjelderVedtak, tittel, fritekst, historikkAktør);
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", dokumentMalType=" + dokumentMalType +
                ", behandlingType=" + behandlingType +
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
        private BehandlingType behandlingType;
        private String tittel;
        private String fritekst;
        private Boolean gjelderVedtak;
        private HistorikkAktør historikkAktør;

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

        public DokumentHendelse.Builder medBehandlingType(BehandlingType behandlingType) {
            this.behandlingType = behandlingType;
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

        public DokumentHendelse build() {
            verifyStateForBuild();
            DokumentHendelse dokumentHendelse = new DokumentHendelse(behandlingId);
            dokumentHendelse.behandlingType = behandlingType;
            dokumentHendelse.ytelseType = ytelseType;
            dokumentHendelse.gjelderVedtak = gjelderVedtak;
            dokumentHendelse.fritekst = fritekst;
            dokumentHendelse.tittel = tittel;
            dokumentHendelse.dokumentMalType = dokumentMalType;
            dokumentHendelse.historikkAktør = historikkAktør;
            return dokumentHendelse;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingId, "behandlingId");
        }

    }

}

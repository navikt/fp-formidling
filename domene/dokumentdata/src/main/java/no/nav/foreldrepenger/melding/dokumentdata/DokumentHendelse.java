package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.kodeverk.BehandlingType;
import no.nav.foreldrepenger.melding.kodeverk.FagsakYtelseType;
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

    @Lob
    @Column
    private String fritekst;


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

    private void setGjelderVedtak(Boolean gjelderVedtak) {
        this.gjelderVedtak = gjelderVedtak;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentHendelse that = (DokumentHendelse) o;
        return gjelderVedtak == that.gjelderVedtak &&
                Objects.equals(id, that.id) &&
                behandlingId.equals(that.behandlingId) &&
                Objects.equals(dokumentMalType, that.dokumentMalType) &&
                behandlingType.equals(that.behandlingType) &&
                ytelseType.equals(that.ytelseType) &&
                Objects.equals(tittel, that.tittel) &&
                Objects.equals(fritekst, that.fritekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingId, dokumentMalType, behandlingType, ytelseType, gjelderVedtak, tittel, fritekst);
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

        public DokumentHendelse build() {
            verifyStateForBuild();
            DokumentHendelse dokumentHendelse = new DokumentHendelse(behandlingId);
            dokumentHendelse.behandlingType = behandlingType;
            dokumentHendelse.ytelseType = ytelseType;
            dokumentHendelse.gjelderVedtak = gjelderVedtak;
            dokumentHendelse.fritekst = fritekst;
            dokumentHendelse.tittel = tittel;
            dokumentHendelse.dokumentMalType = dokumentMalType;
            return dokumentHendelse;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(behandlingId, "behandlingId");
        }

    }

}

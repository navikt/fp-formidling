package no.nav.foreldrepenger.melding.dokumentdata;

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

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.jpa.BaseEntitet;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

@Entity(name = "SaksbehandlerTekst")
@Table(name = "SAKSBEHANDLER_TEKST")
public class SaksbehandlerTekst extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SAKSBEHANDLER_TEKST")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "vedtaksbrev", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + Vedtaksbrev.DISCRIMINATOR + "'"))
    private Vedtaksbrev vedtaksbrev;

    @Column(name = "avklar_fritekst")
    private String avklarFritekst;

    @Column
    private String tittel;

    @Column
    private String fritekst;

    private SaksbehandlerTekst() {
        // for hibernate
    }

    public Long getId() {
        return id;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public Vedtaksbrev getVedtaksbrev() {
        return vedtaksbrev;
    }

    public String getAvklarFritekst() {
        return avklarFritekst;
    }

    public String getTittel() {
        return tittel;
    }

    public String getFritekst() {
        return fritekst;
    }

    public void setVedtaksbrev(Vedtaksbrev vedtaksbrev) {
        this.vedtaksbrev = vedtaksbrev;
    }

    public void setAvklarFritekst(String avklarFritekst) {
        this.avklarFritekst = avklarFritekst;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaksbehandlerTekst that = (SaksbehandlerTekst) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(behandlingUuid, that.behandlingUuid) &&
                Objects.equals(vedtaksbrev, that.vedtaksbrev) &&
                Objects.equals(avklarFritekst, that.avklarFritekst) &&
                Objects.equals(tittel, that.tittel) &&
                Objects.equals(fritekst, that.fritekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingUuid, vedtaksbrev, avklarFritekst, tittel, fritekst);
    }

    @Override
    public String toString() {
        return "SaksbehandlerTekst{" +
                "id=" + id +
                ", behandlingUuid=" + behandlingUuid +
                ", vedtaksbrev=" + vedtaksbrev +
                ", avslagarsakFritekst='" + avklarFritekst + '\'' +
                ", tittel='" + tittel + '\'' +
                ", fritekst='" + fritekst + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderEndreEksisterende(SaksbehandlerTekst saksbehandlerTekst) {
        return new Builder(saksbehandlerTekst);
    }

    public static final class Builder {
        private SaksbehandlerTekst saksbehandlerTekst = new SaksbehandlerTekst();

        private Builder() {
        }

        private Builder(SaksbehandlerTekst gammelSaksbehandlerTekst) {
            saksbehandlerTekst = gammelSaksbehandlerTekst;
        }

        public Builder medBehandlingUuid(UUID behandlingUuid) {
            this.saksbehandlerTekst.behandlingUuid = behandlingUuid;
            return this;
        }

        public Builder medVedtaksbrev(Vedtaksbrev vedtaksbrev) {
            this.saksbehandlerTekst.vedtaksbrev = vedtaksbrev;
            return this;
        }

        public Builder medAvklarFritekst(String avklarFritekst) {
            this.saksbehandlerTekst.avklarFritekst = avklarFritekst;
            return this;
        }

        public Builder medTittel(String tittel) {
            this.saksbehandlerTekst.tittel = tittel;
            return this;
        }

        public Builder medFritekst(String fritekst) {
            this.saksbehandlerTekst.fritekst = fritekst;
            return this;
        }

        public SaksbehandlerTekst build() {
            verifyStateForBuild();
            return saksbehandlerTekst;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(saksbehandlerTekst.behandlingUuid, "behandlingUuid");
        }
    }
}

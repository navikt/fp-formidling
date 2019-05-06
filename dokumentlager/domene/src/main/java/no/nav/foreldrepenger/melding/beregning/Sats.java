package no.nav.foreldrepenger.melding.beregning;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.dokumentdata.BaseEntitet;
import no.nav.vedtak.felles.jpa.tid.DatoIntervallEntitet;

@Entity(name = "Sats")
@Table(name = "SATS")
public class Sats extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SATS")
    private Long id;

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    @Column(name = "verdi", nullable = false)
    private long verdi;

    @Embedded
    DatoIntervallEntitet periode;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "sats_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + SatsType.DISCRIMINATOR + "'"))
    private SatsType satsType = SatsType.UDEFINERT;

    @SuppressWarnings("unused")
    private Sats() {
        // For hibernate
    }

    public Sats(SatsType satsType, DatoIntervallEntitet periode, Long verdi) {
        Objects.requireNonNull(satsType, "satsType må være satt");
        Objects.requireNonNull(periode, "periode må være satt");
        Objects.requireNonNull(verdi, "verdi  må være satt");
        this.setSatsType(satsType);
        this.periode = periode;
        this.verdi = verdi;
    }

    public long getVerdi() {
        return verdi;
    }

    public DatoIntervallEntitet getPeriode() {
        return periode;
    }

    public SatsType getSatsType() {
        return Objects.equals(SatsType.UDEFINERT, satsType) ? null : satsType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sats)) {
            return false;
        }
        Sats annen = (Sats) o;

        return Objects.equals(this.getSatsType(), annen.getSatsType())
                && Objects.equals(this.periode, annen.periode)
                && Objects.equals(this.verdi, annen.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSatsType(), periode, verdi);
    }

    private void setSatsType(SatsType satsType) {
        this.satsType = satsType == null ? SatsType.UDEFINERT : satsType;
    }
}

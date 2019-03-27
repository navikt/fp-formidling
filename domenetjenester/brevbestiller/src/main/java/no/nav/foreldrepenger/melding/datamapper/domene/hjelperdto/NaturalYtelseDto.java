package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.time.LocalDate;
import java.util.Objects;

public class NaturalYtelseDto {
    public enum NaturalYtelseEndringStatus {
        START,
        STOPP,
        INGEN_ENDRING
    }

    private LocalDate naturalYtelseEndringDato;
    private NaturalYtelseEndringStatus naturalYtelseEndringStatus;
    private Long naturalYtelseDagsats;
    /**
     * Felt brukt kun for å sammenligne NaturalYtelseDto
     */
    private Long periodeDagsats;

    public LocalDate getNaturalYtelseEndringDato() {
        return naturalYtelseEndringDato;
    }

    public void setNaturalYtelseEndringDato(LocalDate naturalYtelseEndringDato) {
        this.naturalYtelseEndringDato = naturalYtelseEndringDato;
    }

    public NaturalYtelseEndringStatus getNaturalYtelseEndringStatus() {
        return naturalYtelseEndringStatus;
    }

    public void setNaturalYtelseEndringStatus(NaturalYtelseEndringStatus naturalYtelseEndringStatus) {
        this.naturalYtelseEndringStatus = naturalYtelseEndringStatus;
    }

    public Long getNaturalYtelseDagsats() {
        return naturalYtelseDagsats;
    }

    public void setNaturalYtelseDagsats(Long naturalYtelseDagsats) {
        this.naturalYtelseDagsats = naturalYtelseDagsats;
    }

    public Long getPeriodeDagsats() {
        return periodeDagsats;
    }

    public void setPeriodeDagsats(Long periodeDagsats) {
        this.periodeDagsats = periodeDagsats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NaturalYtelseDto that = (NaturalYtelseDto) o;
        return Objects.equals(periodeDagsats, that.periodeDagsats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeDagsats);
    }
}

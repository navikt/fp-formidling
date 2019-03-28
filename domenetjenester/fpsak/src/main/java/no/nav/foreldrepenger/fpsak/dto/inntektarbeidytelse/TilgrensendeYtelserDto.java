package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class TilgrensendeYtelserDto implements Comparable<TilgrensendeYtelserDto> {
    private String relatertYtelseType;
    private LocalDate periodeFraDato;
    private LocalDate periodeTilDato;
    private String status;
    private String saksNummer;

    public TilgrensendeYtelserDto() {
    }

    public String getRelatertYtelseType() {
        return relatertYtelseType;
    }

    public void setRelatertYtelseType(String relatertYtelseType) {
        this.relatertYtelseType = relatertYtelseType;
    }

    public LocalDate getPeriodeFraDato() {
        return periodeFraDato;
    }

    public void setPeriodeFraDato(LocalDate periodeFraDato) {
        this.periodeFraDato = periodeFraDato;
    }

    public LocalDate getPeriodeTilDato() {
        return periodeTilDato;
    }

    public void setPeriodeTilDato(LocalDate periodeTilDato) {
        this.periodeTilDato = periodeTilDato;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaksNummer() {
        return saksNummer;
    }

    public void setSaksNummer(String saksNummer) {
        this.saksNummer = saksNummer;
    }

    @Override
    public int compareTo(TilgrensendeYtelserDto other) {
        return Comparator.nullsLast(LocalDate::compareTo).compare(other.getPeriodeFraDato(), this.getPeriodeFraDato());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TilgrensendeYtelserDto)) {
            return false;
        }
        TilgrensendeYtelserDto that = (TilgrensendeYtelserDto) o;
        return Objects.equals(relatertYtelseType, that.relatertYtelseType) &&
                Objects.equals(periodeFraDato, that.periodeFraDato) &&
                Objects.equals(periodeTilDato, that.periodeTilDato) &&
                Objects.equals(status, that.status) &&
                Objects.equals(saksNummer, that.saksNummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relatertYtelseType, periodeFraDato, periodeTilDato, status, saksNummer);
    }
}

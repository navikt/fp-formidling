package no.nav.foreldrepenger.fpsak.dto.ytelsefordeling;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.PeriodeDto;

public class YtelseFordelingDto {
    private List<PeriodeDto> ikkeOmsorgPerioder;
    private LocalDate endringsdato;
    private int gjeldendeDekningsgrad;
    private LocalDate førsteUttaksdato;

    private YtelseFordelingDto() {
    }

    public List<PeriodeDto> getIkkeOmsorgPerioder() {
        return ikkeOmsorgPerioder;
    }

    public LocalDate getEndringsdato() {
        return endringsdato;
    }

    public int getGjeldendeDekningsgrad() {
        return gjeldendeDekningsgrad;
    }

    public LocalDate getFørsteUttaksdato() {
        return førsteUttaksdato;
    }

    public static class Builder {

        private final YtelseFordelingDto kladd = new YtelseFordelingDto();

        public Builder medIkkeOmsorgPerioder(List<PeriodeDto> ikkeOmsorgPerioder) {
            kladd.ikkeOmsorgPerioder = ikkeOmsorgPerioder;
            return this;
        }

        public Builder medEndringsdato(LocalDate endringsDato) {
            kladd.endringsdato = endringsDato;
            return this;
        }

        public Builder medGjeldendeDekningsgrad(int gjeldendeDekningsgrad) {
            kladd.gjeldendeDekningsgrad = gjeldendeDekningsgrad;
            return this;
        }

        public Builder medFørsteUttaksdato(LocalDate førsteUttaksdato) {
            kladd.førsteUttaksdato = førsteUttaksdato;
            return this;
        }

        public YtelseFordelingDto build() {
            return kladd;
        }
    }
}

package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.time.LocalDate;
import java.util.Arrays;

public class BeregningsresultatMedUttaksplanDto {
    private boolean sokerErMor;
    private LocalDate opphoersdato;
    private BeregningsresultatPeriodeDto[] perioder;
    private Boolean skalHindreTilbaketrekk;

    public boolean getSokerErMor() {
        return sokerErMor;
    }

    public LocalDate getOpphoersdato() {
        return opphoersdato;
    }

    public BeregningsresultatPeriodeDto[] getPerioder() {
        return Arrays.copyOf(perioder, perioder.length);
    }

    public Boolean getSkalHindreTilbaketrekk() {
        return skalHindreTilbaketrekk;
    }

    public void setSokerErMor(boolean sokerErMor) {
        this.sokerErMor = sokerErMor;
    }

    public void setOpphoersdato(LocalDate opphoersdato) {
        this.opphoersdato = opphoersdato;
    }

    public void setPerioder(BeregningsresultatPeriodeDto[] perioder) {
        this.perioder = perioder;
    }

    public void setSkalHindreTilbaketrekk(Boolean skalHindreTilbaketrekk) {
        this.skalHindreTilbaketrekk = skalHindreTilbaketrekk;
    }
}

package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.util.Arrays;

public class BeregningsresultatMedUttaksplanDto {
    private BeregningsresultatPeriodeDto[] perioder;

    public BeregningsresultatPeriodeDto[] getPerioder() {
        return Arrays.copyOf(perioder, perioder.length);
    }

    public void setPerioder(BeregningsresultatPeriodeDto[] perioder) {
        this.perioder = perioder;
    }

}

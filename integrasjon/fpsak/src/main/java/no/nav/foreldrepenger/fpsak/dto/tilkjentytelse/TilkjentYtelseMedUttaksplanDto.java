package no.nav.foreldrepenger.fpsak.dto.tilkjentytelse;

import java.util.Arrays;

public class TilkjentYtelseMedUttaksplanDto {
    private TilkjentYtelsePeriodeDto[] perioder;

    public TilkjentYtelsePeriodeDto[] getPerioder() {
        return Arrays.copyOf(perioder, perioder.length);
    }

    public void setPerioder(TilkjentYtelsePeriodeDto[] perioder) {
        this.perioder = perioder;
    }

}

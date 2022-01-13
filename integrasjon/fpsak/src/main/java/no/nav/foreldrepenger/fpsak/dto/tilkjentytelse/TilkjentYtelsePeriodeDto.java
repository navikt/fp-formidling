package no.nav.foreldrepenger.fpsak.dto.tilkjentytelse;

import java.time.LocalDate;
import java.util.Arrays;

public class TilkjentYtelsePeriodeDto {
    private LocalDate fom;
    private LocalDate tom;
    private int dagsats;
    private TilkjentYtelseAndelDto[] andeler;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public int getDagsats() {
        return dagsats;
    }

    public TilkjentYtelseAndelDto[] getAndeler() {
        return Arrays.copyOf(andeler, andeler.length);
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }

    public void setDagsats(int dagsats) {
        this.dagsats = dagsats;
    }

    public void setAndeler(TilkjentYtelseAndelDto[] andeler) {
        this.andeler = andeler;
    }
}

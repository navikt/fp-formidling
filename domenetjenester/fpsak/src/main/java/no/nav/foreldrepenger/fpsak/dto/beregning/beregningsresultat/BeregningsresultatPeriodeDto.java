package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.time.LocalDate;
import java.util.Arrays;

public class BeregningsresultatPeriodeDto {
    private LocalDate fom;
    private LocalDate tom;
    private int dagsats;
    private BeregningsresultatPeriodeAndelDto[] andeler;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public int getDagsats() {
        return dagsats;
    }

    public BeregningsresultatPeriodeAndelDto[] getAndeler() {
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

    public void setAndeler(BeregningsresultatPeriodeAndelDto[] andeler) {
        this.andeler = andeler;
    }
}

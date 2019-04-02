package no.nav.foreldrepenger.melding.beregning;

import java.time.LocalDate;

public class Beregning {
    private long beregnetTilkjentYtelse;
    private String verdi;
    private LocalDate beregnetTidspunkt;

    public long getBeregnetTilkjentYtelse() {
        return beregnetTilkjentYtelse;
    }

    public String getVerdi() {
        return verdi;
    }

    public LocalDate getBeregnetTidspunkt() {
        return beregnetTidspunkt;
    }
}

package no.nav.foreldrepenger.fpformidling.tilkjentytelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

public class TilkjentYtelsePeriode {

    private final Long dagsats;
    private final DatoIntervall periode;
    private final Long utbetaltTilSøker;
    private final List<TilkjentYtelseAndel> andeler;

    private TilkjentYtelsePeriode(Builder builder) {
        dagsats = builder.dagsats;
        periode = builder.periode;
        andeler = builder.andeler;
        utbetaltTilSøker = ((Integer) andeler.stream()
            .filter(TilkjentYtelseAndel::erBrukerMottaker)
            .map(TilkjentYtelseAndel::getUtbetalesTilBruker)
            .mapToInt(Integer::intValue)
            .sum()).longValue();
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
    }

    public Long getUtbetaltTilSøker() {
        return utbetaltTilSøker;
    }

    public LocalDate getPeriodeFom() {
        return periode.getFomDato();
    }

    public DatoIntervall getPeriode() {
        return periode;
    }

    public LocalDate getPeriodeTom() {
        return periode.getTomDato();
    }

    public List<TilkjentYtelseAndel> getAndeler() {
        return Collections.unmodifiableList(andeler);
    }

    public static final class Builder {
        private Long dagsats;
        private DatoIntervall periode;
        private List<TilkjentYtelseAndel> andeler = new ArrayList<>();

        private Builder() {
        }

        public Builder medDagsats(Long dagsats) {
            this.dagsats = dagsats;
            return this;
        }

        public Builder medPeriode(DatoIntervall periode) {
            this.periode = periode;
            return this;
        }

        public Builder medAndeler(List<TilkjentYtelseAndel> andelerList) {
            this.andeler = andelerList;
            return this;
        }

        public TilkjentYtelsePeriode build() {
            return new TilkjentYtelsePeriode(this);
        }
    }
}

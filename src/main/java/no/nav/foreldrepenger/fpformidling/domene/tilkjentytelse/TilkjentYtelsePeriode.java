package no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

public class TilkjentYtelsePeriode {

    private final Long dagsats;
    private final DatoIntervall periode;
    private final List<TilkjentYtelseAndel> andeler;

    private TilkjentYtelsePeriode(Builder builder) {
        dagsats = builder.dagsats;
        periode = builder.periode;
        andeler = builder.andeler;
    }

    @Override
    public String toString() {
        return "TilkjentYtelsePeriode{" + "dagsats=" + dagsats + ", periode=" + periode + ", andeler=" + andeler + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TilkjentYtelsePeriode that = (TilkjentYtelsePeriode) o;
        return Objects.equals(dagsats, that.dagsats) && Objects.equals(periode, that.periode) && likeAndeler(andeler, that.andeler);
    }

    private boolean likeAndeler(List<TilkjentYtelseAndel> liste1, List<TilkjentYtelseAndel> liste2) {
        if (liste1 != null && liste2 != null) {
            return liste1.size() == liste2.size() && liste1.containsAll(liste2);
        }
        return Objects.equals(liste1, liste2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dagsats, periode, andeler);
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
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

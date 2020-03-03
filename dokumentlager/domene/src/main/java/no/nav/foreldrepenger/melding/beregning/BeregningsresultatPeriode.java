package no.nav.foreldrepenger.melding.beregning;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsresultatPeriode {

    private Long dagsats;
    private DatoIntervall periode;
    private Integer utbetaltTilSoker;
    private List<BeregningsresultatAndel> beregningsresultatAndelList;

    private BeregningsresultatPeriode(Builder builder) {
        dagsats = builder.dagsats;
        periode = builder.periode;
        beregningsresultatAndelList = builder.beregningsresultatAndelList;
        utbetaltTilSoker = beregningsresultatAndelList.stream().filter(x -> x.erBrukerMottaker())
                .map(x -> x.getDagsats())
                .collect(Collectors.summingInt(Integer::intValue));

    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
    }
    public Integer getUtbetaltTilSoker() {
        return utbetaltTilSoker;
    }

    public LocalDate getBeregningsresultatPeriodeFom() {
        return periode.getFomDato();
    }

    public DatoIntervall getPeriode() {
        return periode;
    }

    public LocalDate getBeregningsresultatPeriodeTom() {
        return periode.getTomDato();
    }

    public List<BeregningsresultatAndel> getBeregningsresultatAndelList() {
        return Collections.unmodifiableList(beregningsresultatAndelList);
    }


    public static final class Builder {
        private Long dagsats;
        private DatoIntervall periode;
        private List<BeregningsresultatAndel> beregningsresultatAndelList = new ArrayList<>();

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

        public Builder medBeregningsresultatAndel(List<BeregningsresultatAndel> beregningsresultatAndelList) {
            this.beregningsresultatAndelList = beregningsresultatAndelList;
            return this;
        }

        public BeregningsresultatPeriode build() {
            return new BeregningsresultatPeriode(this);
        }
    }
}

package no.nav.foreldrepenger.melding.beregning;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsresultatPeriode {
    private Long dagsats;

    //Object
    private DatoIntervall periode;
    private List<BeregningsresultatAndel> beregningsresultatAndelList = new ArrayList<>();

    public Long getDagsats() {
        return dagsats;
    }

    public LocalDate getBeregningsresultatPeriodeFom() {
        return periode.getFomDato();
    }

    public LocalDate getBeregningsresultatPeriodeTom() {
        return periode.getTomDato();
    }

    public List<BeregningsresultatAndel> getBeregningsresultatAndelList() {
        return Collections.unmodifiableList(beregningsresultatAndelList);
    }
}

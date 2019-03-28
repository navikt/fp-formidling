package no.nav.foreldrepenger.melding.behandling.beregning;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsresultatPeriode {
    private String dagsats;

    //Object
    private DatoIntervall periode;

    public String getDagsats() {
        return dagsats;
    }

    public LocalDate getBeregningsresultatPeriodeFom() {
        return periode.getFomDato();
    }

    public LocalDate getBeregningsresultatPeriodeTom() {
        return periode.getTomDato();
    }
}

package no.nav.foreldrepenger.melding.typer;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;

public interface DatoIntervall extends Comparable<DatoIntervall> {
    Dato getFom();

    Dato getTom();

    @Deprecated
    LocalDate getFomDato();

    @Deprecated
    LocalDate getTomDato();

    static DatoIntervall fraOgMedTilOgMed(LocalDate fomDato, LocalDate tomDato) {
        return new DatoIntervallImpl(fomDato, tomDato);
    }

    static DatoIntervall fraOgMed(LocalDate fomDato) {
        return new DatoIntervallImpl(fomDato, TIDENES_ENDE);
    }

    @Override
    default int compareTo(DatoIntervall o) {
        int compareFom = this.getFomDato().compareTo(o.getFomDato());
        int compareTom = this.getTomDato().compareTo(o.getTomDato());
        return compareFom != 0 ? compareFom : compareTom;
    }
}

package no.nav.foreldrepenger.fpformidling.domene.typer;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;

public interface DatoIntervall extends Comparable<DatoIntervall> {
    Dato getFom();

    Dato getTom();

    LocalDate getFomDato();

    LocalDate getTomDato();

    static DatoIntervall fraOgMedTilOgMed(Dato fomDato, Dato tomDato) {
        return new DatoIntervallImpl(fomDato, tomDato);
    }

    static DatoIntervall fraOgMedTilOgMed(LocalDate fomDato, LocalDate tomDato) {
        return new DatoIntervallImpl(fomDato, tomDato);
    }

    static DatoIntervall fraOgMed(LocalDate fomDato) {
        return new DatoIntervallImpl(fomDato, TIDENES_ENDE);
    }

    @Override
    default int compareTo(DatoIntervall o) {
        var compareFom = this.getFomDato().compareTo(o.getFomDato());
        var compareTom = this.getTomDato().compareTo(o.getTomDato());
        return compareFom != 0 ? compareFom : compareTom;
    }
}

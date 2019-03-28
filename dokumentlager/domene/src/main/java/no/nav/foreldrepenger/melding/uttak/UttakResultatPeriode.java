package no.nav.foreldrepenger.melding.uttak;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class UttakResultatPeriode {
    private String periodeResultatÅrsak; //Kodeliste.PeriodeResultatÅrsak
    private String overstyrtPerioder;
    private DatoIntervall tidsperiode;
    private String graderingAvslagÅrsak; //Kodeliste.GraderingAvslagÅrsak
    private String uttakUtsettelseType; //Kodeliste.UttakUtsettelseType
    private String periodeResultatType; //Kodeliste.PeriodeResultatType
    private List<UttakResultatPeriodeAktivitet> aktiviteter = new ArrayList<>();

    public String getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public String getOverstyrtPerioder() {
        return overstyrtPerioder;
    }

    public LocalDate getFom() {
        return tidsperiode.getFomDato();
    }

    public LocalDate getTom() {
        return tidsperiode.getTomDato();
    }

    public String getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak;
    }

    public String getUttakUtsettelseType() {
        return uttakUtsettelseType;
    }

    public String getPeriodeResultatType() {
        return periodeResultatType;
    }

    public List<UttakResultatPeriodeAktivitet> getAktiviteter() {
        return aktiviteter;
    }
}

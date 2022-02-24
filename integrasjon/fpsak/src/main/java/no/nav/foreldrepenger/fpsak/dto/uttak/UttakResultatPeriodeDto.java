package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class UttakResultatPeriodeDto {

    private LocalDate fom;
    private LocalDate tom;
    private List<UttakResultatPeriodeAktivitetDto> aktiviteter = new ArrayList<>();
    private PeriodeResultatType periodeResultatType;
    private KodeDto periodeResultatÅrsak;
    private KodeDto graderingAvslagÅrsak;
    private String periodeResultatÅrsakLovhjemmel;
    private String graderingsAvslagÅrsakLovhjemmel;

    private UttakResultatPeriodeDto() {

    }

    public KodeDto getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public KodeDto getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }


    public List<UttakResultatPeriodeAktivitetDto> getAktiviteter() {
        return aktiviteter;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public String getPeriodeResultatÅrsakLovhjemmel() {
        return periodeResultatÅrsakLovhjemmel;
    }

    public String getGraderingsAvslagÅrsakLovhjemmel() {
        return graderingsAvslagÅrsakLovhjemmel;
    }

    @JsonProperty("gradertAktivitet")
    public UttakResultatPeriodeAktivitetDto getGradertAktivitet() {
        return aktiviteter.stream().filter(UttakResultatPeriodeAktivitetDto::isGradering).findFirst().orElse(null);
    }
}

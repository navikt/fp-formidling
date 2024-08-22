package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;

public class UttakResultatPeriodeDto {

    private LocalDate fom;
    private LocalDate tom;
    private List<UttakResultatPeriodeAktivitetDto> aktiviteter = new ArrayList<>();
    private PeriodeResultatType periodeResultatType;
    private String periodeResultatÅrsak;
    private String graderingAvslagÅrsak;
    private String periodeResultatÅrsakLovhjemmel;
    private String graderingsAvslagÅrsakLovhjemmel;
    private LocalDate tidligstMottattDato;
    private boolean erUtbetalingRedusertTilMorsStillingsprosent;

    private UttakResultatPeriodeDto() {

    }

    public String getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public String getGraderingAvslagÅrsak() {
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

    public LocalDate getTidligstMottattDato() {
        return tidligstMottattDato;
    }

    public boolean getErUtbetalingRedusertTilMorsStillingsprosent() {
        return erUtbetalingRedusertTilMorsStillingsprosent;
    }

    @JsonProperty("gradertAktivitet")
    public UttakResultatPeriodeAktivitetDto getGradertAktivitet() {
        return aktiviteter.stream().filter(UttakResultatPeriodeAktivitetDto::isGradering).findFirst().orElse(null);
    }
}

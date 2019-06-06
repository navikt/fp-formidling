package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class UttakResultatPeriodeDto {

    private LocalDate fom;
    private LocalDate tom;
    private List<UttakResultatPeriodeAktivitetDto> aktiviteter = new ArrayList<>();
    private KodeDto periodeResultatType;
    private String begrunnelse;
    private KodeDto periodeResultatÅrsak;
    private KodeDto manuellBehandlingÅrsak;
    private KodeDto graderingAvslagÅrsak;
    private boolean flerbarnsdager;
    private boolean samtidigUttak;
    private BigDecimal samtidigUttaksprosent;
    private boolean graderingInnvilget;
    private KodeDto periodeType;
    private KodeDto utsettelseType;
    private KodeDto oppholdÅrsak;
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

    public boolean isFlerbarnsdager() {
        return flerbarnsdager;
    }

    public boolean isSamtidigUttak() {
        return samtidigUttak;
    }

    public boolean isGraderingInnvilget() {
        return graderingInnvilget;
    }

    public KodeDto getManuellBehandlingÅrsak() {
        return manuellBehandlingÅrsak;
    }

    public List<UttakResultatPeriodeAktivitetDto> getAktiviteter() {
        return aktiviteter;
    }

    public void leggTilAktivitet(UttakResultatPeriodeAktivitetDto aktivitetDto) {
        aktiviteter.add(aktivitetDto);
    }

    public KodeDto getPeriodeResultatType() {
        return periodeResultatType;
    }

    public String getBegrunnelse() {
        return begrunnelse;
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

    public KodeDto getPeriodeType() {
        return periodeType;
    }

    public KodeDto getUtsettelseType() {
        return utsettelseType;
    }

    public BigDecimal getSamtidigUttaksprosent() {
        return samtidigUttaksprosent;
    }

    public KodeDto getOppholdÅrsak() {
        return oppholdÅrsak;
    }
}

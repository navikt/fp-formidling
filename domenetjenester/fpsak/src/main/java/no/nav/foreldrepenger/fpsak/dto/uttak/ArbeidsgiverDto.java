package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.util.InputValideringRegex;

public class ArbeidsgiverDto {

    @Pattern(regexp = "[\\d]{9}")
    private String identifikator;

    private String aktørId;

    private LocalDate fødselsdato;

    @Pattern(regexp = InputValideringRegex.FRITEKST)
    @Size(max = 200)
    private String navn;

    ArbeidsgiverDto() {

    }

    private ArbeidsgiverDto(String identifikator, String navn, String aktørId, LocalDate fødselsdato) {
        this.identifikator = identifikator;
        this.navn = navn;
        this.aktørId = aktørId;
        this.fødselsdato = fødselsdato;
    }

    public static ArbeidsgiverDto virksomhet(String identifikator, String navn) {
        return new ArbeidsgiverDto(identifikator, navn, null, null);
    }

    public static ArbeidsgiverDto person(String navn, String aktørId, LocalDate fødselsdato) {
        return new ArbeidsgiverDto(null, navn, aktørId, fødselsdato);
    }

    public String getIdentifikator() {
        return identifikator;
    }

    public String getNavn() {
        return navn;
    }

    public String getAktørId() {
        return aktørId;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    @JsonProperty("virksomhet")
    public boolean isVirksomhet() {
        return aktørId == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbeidsgiverDto that = (ArbeidsgiverDto) o;
        return Objects.equals(identifikator, that.identifikator) &&
                Objects.equals(aktørId, that.aktørId) &&
                Objects.equals(fødselsdato, that.fødselsdato) &&
                Objects.equals(navn, that.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifikator, aktørId, fødselsdato, navn);
    }
}

package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class UtsettelsePeriodeDto {

    @JsonProperty("fom")
    private LocalDate fom;

    @JsonProperty("tom")
    private LocalDate tom;

    @JsonProperty("utsettelseArsak")
    private KodeDto utsettelseÅrsak;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public KodeDto getUtsettelseArsak() {
        return utsettelseÅrsak;
    }
}

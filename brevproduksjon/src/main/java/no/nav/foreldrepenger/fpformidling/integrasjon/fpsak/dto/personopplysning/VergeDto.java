package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.personopplysning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VergeDto (String navn, String aktoerId, String organisasjonsnummer, LocalDate gyldigFom, LocalDate gyldigTom ){}

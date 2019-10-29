package no.nav.foreldrepenger.fpsak.dto.personopplysning;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VergeDto {
    private String navn;
    private String fnr;
    private String organisasjonsnummer;

    public VergeDto() { //NOSONAR
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }
}

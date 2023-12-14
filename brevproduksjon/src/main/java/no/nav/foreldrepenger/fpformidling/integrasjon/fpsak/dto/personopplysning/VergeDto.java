package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.personopplysning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VergeDto {
    private String navn;
    private String aktoerId;
    private String organisasjonsnummer;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;


    public LocalDate getGydldigFom() {
        return gyldigFom;
    }
    public void setGyldigFom(LocalDate gyldigFom) {
        this.gyldigFom = gyldigFom;
    }
    public LocalDate getGyldigTom() {
        return gyldigTom;
    }
    public void setGyldigTom(LocalDate gyldigTom) {
        this.gyldigTom = gyldigTom;
    }
    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }
}

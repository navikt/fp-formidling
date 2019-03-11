package no.nav.foreldrepenger.fpsak.dto.personopplysning;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VergeDto {
    private String navn;
    private String fnr;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private KodeDto vergeType;
    private String mandatTekst;
    private boolean sokerErUnderTvungenForvaltning;
    private boolean sokerErKontaktPerson;
    private boolean vergeErKontaktPerson;

    public VergeDto() { //NOSONAR
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setGyldigFom(LocalDate gyldigFom) {
        this.gyldigFom = gyldigFom;
    }

    public void setGyldigTom(LocalDate gyldigTom) {
        this.gyldigTom = gyldigTom;
    }

    public void setVergeType(KodeDto vergeType) {
        this.vergeType = vergeType;
    }

    public void setMandatTekst(String mandatTekst) {
        this.mandatTekst = mandatTekst;
    }

    public void setSokerErUnderTvungenForvaltning(boolean sokerErUnderTvungenForvaltning) {
        this.sokerErUnderTvungenForvaltning = sokerErUnderTvungenForvaltning;
    }

    public void setSokerErKontaktPerson(boolean sokerErKontaktPerson) {
        this.sokerErKontaktPerson = sokerErKontaktPerson;
    }

    public void setVergeErKontaktPerson(boolean vergeErKontaktPerson) {
        this.vergeErKontaktPerson = vergeErKontaktPerson;
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

    public LocalDate getGyldigFom() {
        return gyldigFom;
    }

    public LocalDate getGyldigTom() {
        return gyldigTom;
    }

    public KodeDto getVergeType() {
        return vergeType;
    }

    public String getMandatTekst() {
        return mandatTekst;
    }

    public boolean getSokerErUnderTvungenForvaltning() {
        return sokerErUnderTvungenForvaltning;
    }

    public boolean getSokerErKontaktPerson() {
        return sokerErKontaktPerson;
    }

    public boolean getVergeErKontaktPerson() {
        return vergeErKontaktPerson;
    }
}

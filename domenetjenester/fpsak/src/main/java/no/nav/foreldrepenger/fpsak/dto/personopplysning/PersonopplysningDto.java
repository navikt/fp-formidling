package no.nav.foreldrepenger.fpsak.dto.personopplysning;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonopplysningDto extends PersonIdentDto {
    private Integer nummer;
    private KodeDto navBrukerKjonn;
    private KodeDto statsborgerskap;
    private KodeDto personstatus;
    private KodeDto sivilstand;
    private String navn;
    private LocalDate dodsdato;
    private LocalDate fodselsdato;
    private List<PersonadresseDto> adresser = new ArrayList<>();

    private KodeDto region;
    private PersonopplysningDto annenPart;
    private PersonopplysningDto ektefelle;
    private List<PersonopplysningDto> barn = new ArrayList<>();
    private List<PersonopplysningDto> barnSoktFor = new ArrayList<>();
    private List<PersonopplysningDto> barnFraTpsRelatertTilSoknad = new ArrayList<>();
    private KodeDto opplysningsKilde;
    private Boolean harVerge;

    public Integer getNummer() {
        return nummer;
    }

    public void setNummer(Integer nummer) {
        this.nummer = nummer;
    }

    public KodeDto getNavBrukerKjonn() {
        return navBrukerKjonn;
    }

    public void setNavBrukerKjonn(KodeDto navBrukerKjonn) {
        this.navBrukerKjonn = navBrukerKjonn;
    }

    public KodeDto getStatsborgerskap() {
        return statsborgerskap;
    }

    public void setStatsborgerskap(KodeDto statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    public KodeDto getPersonstatus() {
        return personstatus;
    }

    public void setPersonstatus(KodeDto personstatus) {
        this.personstatus = personstatus;
    }

    public KodeDto getSivilstand() {
        return sivilstand;
    }

    public void setSivilstand(KodeDto sivilstand) {
        this.sivilstand = sivilstand;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public LocalDate getDodsdato() {
        return dodsdato;
    }

    public void setDodsdato(LocalDate dodsdato) {
        this.dodsdato = dodsdato;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    public void setFodselsdato(LocalDate fodselsdato) {
        this.fodselsdato = fodselsdato;
    }

    public List<PersonadresseDto> getAdresser() {
        return adresser;
    }

    public void setAdresser(List<PersonadresseDto> adresser) {
        this.adresser = adresser;
    }

    public KodeDto getRegion() {
        return region;
    }

    public void setRegion(KodeDto region) {
        this.region = region;
    }

    public PersonopplysningDto getAnnenPart() {
        return annenPart;
    }

    public void setAnnenPart(PersonopplysningDto annenPart) {
        this.annenPart = annenPart;
    }

    public PersonopplysningDto getEktefelle() {
        return ektefelle;
    }

    public void setEktefelle(PersonopplysningDto ektefelle) {
        this.ektefelle = ektefelle;
    }

    public List<PersonopplysningDto> getBarn() {
        return barn;
    }

    public void setBarn(List<PersonopplysningDto> barn) {
        this.barn = barn;
    }

    public List<PersonopplysningDto> getBarnSoktFor() {
        return barnSoktFor;
    }

    public void setBarnSoktFor(List<PersonopplysningDto> barnSoktFor) {
        this.barnSoktFor = barnSoktFor;
    }

    public List<PersonopplysningDto> getBarnFraTpsRelatertTilSoknad() {
        return barnFraTpsRelatertTilSoknad;
    }

    public void setBarnFraTpsRelatertTilSoknad(List<PersonopplysningDto> barnFraTpsRelatertTilSoknad) {
        this.barnFraTpsRelatertTilSoknad = barnFraTpsRelatertTilSoknad;
    }

    public KodeDto getOpplysningsKilde() {
        return opplysningsKilde;
    }

    public void setOpplysningsKilde(KodeDto opplysningsKilde) {
        this.opplysningsKilde = opplysningsKilde;
    }

    public Boolean getHarVerge() {
        return harVerge;
    }

    public void setHarVerge(Boolean harVerge) {
        this.harVerge = harVerge;
    }

    @Override
    public String toString() {
        return "PersonopplysningDto{" +
                "nummer=" + nummer +
                ", navBrukerKjonn=" + navBrukerKjonn +
                ", statsborgerskap=" + statsborgerskap +
                ", personstatus=" + personstatus +
                ", sivilstand=" + sivilstand +
                ", navn='" + navn + '\'' +
                ", dodsdato=" + dodsdato +
                ", fodselsdato=" + fodselsdato +
                ", adresser=" + adresser +
                ", region=" + region +
                ", annenPart=" + annenPart +
                ", ektefelle=" + ektefelle +
                ", barn=" + barn +
                ", barnSoktFor=" + barnSoktFor +
                ", barnFraTpsRelatertTilSoknad=" + barnFraTpsRelatertTilSoknad +
                ", opplysningsKilde=" + opplysningsKilde +
                ", harVerge=" + harVerge +
                '}';
    }
}

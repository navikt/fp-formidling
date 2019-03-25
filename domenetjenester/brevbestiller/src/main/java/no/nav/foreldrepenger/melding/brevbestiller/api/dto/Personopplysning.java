package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;

public class Personopplysning {
    private String fnr;
    private Long aktoerId;
    private KodeDto navBrukerKjonn;
    private String navn;
    private Boolean harVerge;

    public Personopplysning(PersonopplysningDto dto) {
        this.fnr = dto.getFnr();
        this.aktoerId = dto.getAktoerId();
        this.navBrukerKjonn = dto.getNavBrukerKjonn();
        this.navn = dto.getNavn();
        this.harVerge = dto.getHarVerge();
    }

    public String getFnr() {
        return fnr;
    }

    public Long getAktoerId() {
        return aktoerId;
    }

    public KodeDto getNavBrukerKjonn() {
        return navBrukerKjonn;
    }

    public String getNavn() {
        return navn;
    }

    public Boolean getHarVerge() {
        return harVerge;
    }
}

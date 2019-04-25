package no.nav.foreldrepenger.melding.personopplysning;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

public class Personopplysning {
    private String fnr;
    private Long aktoerId;
    private NavBrukerKjønn navBrukerKjonn;
    private String navn;
    private Boolean harVerge;

    public Personopplysning(PersonopplysningDto dto, KodeverkRepository kodeverkRepository) {
        this.fnr = dto.getFnr();
        this.aktoerId = dto.getAktoerId();
        this.navBrukerKjonn = kodeverkRepository.finn(NavBrukerKjønn.class, dto.getNavBrukerKjonn().getKode());
        this.navn = dto.getNavn();
        this.harVerge = dto.getHarVerge();
    }

    public String getFnr() {
        return fnr;
    }

    public Long getAktoerId() {
        return aktoerId;
    }

    public NavBrukerKjønn getNavBrukerKjonn() {
        return navBrukerKjonn;
    }

    public String getNavn() {
        return navn;
    }

    public Boolean getHarVerge() {
        return harVerge;
    }
}

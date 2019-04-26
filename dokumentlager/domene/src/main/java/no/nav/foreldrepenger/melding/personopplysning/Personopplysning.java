package no.nav.foreldrepenger.melding.personopplysning;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;

public class Personopplysning {
    private String fnr;
    private Long aktoerId;
    private NavBrukerKjønn navBrukerKjonn;
    private String navn;
    private Boolean harVerge;

    public Personopplysning(String fnr, Long aktoerId, NavBrukerKjønn navBrukerKjonn, String navn, Boolean harVerge) {
        this.fnr = fnr;
        this.aktoerId = aktoerId;
        this.navBrukerKjonn = navBrukerKjonn;
        this.navn = navn;
        this.harVerge = harVerge;
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

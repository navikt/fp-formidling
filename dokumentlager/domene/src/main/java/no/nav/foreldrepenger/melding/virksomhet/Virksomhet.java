package no.nav.foreldrepenger.melding.virksomhet;

public class Virksomhet {
    private String navn;
    private String orgnr;

    public Virksomhet(String navn, String orgnr) {
        this.navn = navn;
        this.orgnr = orgnr;
    }

    public String getNavn() {
        return navn;
    }

    public String getOrgnr() {
        return orgnr;
    }
}

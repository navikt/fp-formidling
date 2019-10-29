package no.nav.foreldrepenger.melding.verge;

public class Verge {

    private String organisasjonsnummer;
    private String navn;
    private String fnr;

    public Verge(String fnr, String organisasjonsnummer,String navn) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.fnr = fnr;
        this.navn = navn;
    }

    public String getFnr() {
        return fnr;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }
}

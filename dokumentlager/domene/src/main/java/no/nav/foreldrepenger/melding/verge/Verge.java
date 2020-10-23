package no.nav.foreldrepenger.melding.verge;

public class Verge {

    private String organisasjonsnummer;
    private String navn;
    private String aktoerId;

    public Verge(String aktoerId, String organisasjonsnummer, String navn) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.aktoerId = aktoerId;
        this.navn = navn;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }
}

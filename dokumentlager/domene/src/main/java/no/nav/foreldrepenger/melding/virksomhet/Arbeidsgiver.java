package no.nav.foreldrepenger.melding.virksomhet;

public class Arbeidsgiver {
    private String navn;
    private String arbeidsgiverReferanse;

    public Arbeidsgiver(String arbeidsgiverReferanse, String navn) {
        this.navn = navn;
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
    }

    public String getNavn() {
        return navn;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }
}

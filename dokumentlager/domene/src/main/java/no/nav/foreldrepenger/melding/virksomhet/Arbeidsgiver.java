package no.nav.foreldrepenger.melding.virksomhet;

public class Arbeidsgiver {
    private String navn;
    private String arbeidsgiverReferanse;

    public Arbeidsgiver(String arbeidsgiverReferanse, String navn) {
        this.navn = navn;
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
    }

    /**
     * Returneer ident for arbeidsgiver. Kan være Org nummer eller Aktør id (dersom arbeidsgiver er en enkelt person -
     * f.eks. for Frilans el.)
     */
    public String getIdentifikator() {
        return arbeidsgiverReferanse;
    }

    public String getNavn() {
        return navn;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }
}

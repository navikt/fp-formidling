package no.nav.foreldrepenger.melding.virksomhet;

import no.nav.foreldrepenger.melding.typer.AktørId;

public class Arbeidsgiver {
    private String navn;
    private Virksomhet virksomhet;
    private AktørId arbeidsgiverAktørId;

    public Virksomhet getVirksomhet() {
        return virksomhet;
    }

    public Arbeidsgiver(String navn, Virksomhet virksomhet, AktørId arbeidsgiverAktørId) {
        this.navn = navn;
        this.virksomhet = virksomhet;
        this.arbeidsgiverAktørId = arbeidsgiverAktørId;
    }
    /**
     * Returneer ident for arbeidsgiver. Kan være Org nummer eller Aktør id (dersom arbeidsgiver er en enkelt person -
     * f.eks. for Frilans el.)
     */
    public String getIdentifikator() {
        if (arbeidsgiverAktørId != null) {
            return arbeidsgiverAktørId.getId();
        }
        return virksomhet.getOrgnr();
    }

    public boolean erAktørId() {
        return this.arbeidsgiverAktørId != null;
    }

    public AktørId getAktørId() {
        return arbeidsgiverAktørId;
    }

    public boolean getErVirksomhet() {
        return this.virksomhet != null;
    }

    public String getNavn() {
        return navn;
    }
}

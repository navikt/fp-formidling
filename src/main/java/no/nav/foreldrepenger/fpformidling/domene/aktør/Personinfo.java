package no.nav.foreldrepenger.fpformidling.domene.aktør;

import static java.util.Objects.requireNonNull;

import no.nav.foreldrepenger.fpformidling.domene.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;

public class Personinfo {
    private AktørId aktørId;

    //Brukt for mapping
    private String navn;
    private boolean registrertDød;
    private PersonIdent personIdent;
    private NavBrukerKjønn kjønn;

    private Personinfo() {
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    public String getNavn() {
        return navn;
    }

    public NavBrukerKjønn getKjønn() {
        return kjønn;
    }

    public boolean isRegistrertDød() {
        return registrertDød;
    }

    public static Builder getbuilder(AktørId aktørId) {
        return new Builder(aktørId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<aktørId=" + aktørId + ">"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static class Builder {
        private Personinfo personinfoMal;

        private Builder(AktørId aktørId) {
            personinfoMal = new Personinfo();
            personinfoMal.aktørId = aktørId;
        }

        public Builder medNavn(String navn) {
            personinfoMal.navn = navn;
            return this;
        }

        public Builder medPersonIdent(PersonIdent fnr) {
            personinfoMal.personIdent = fnr;
            return this;
        }

        public Builder medRegistrertDød(boolean registrertDød) {
            personinfoMal.registrertDød = registrertDød;
            return this;
        }

        public Builder medNavBrukerKjønn(NavBrukerKjønn kjønn) {
            personinfoMal.kjønn = kjønn;
            return this;
        }


        public Personinfo build() {
            requireNonNull(personinfoMal.aktørId, "Navbruker må ha aktørId"); //$NON-NLS-1$
            requireNonNull(personinfoMal.personIdent, "Navbruker må ha fødselsnummer"); //$NON-NLS-1$
            requireNonNull(personinfoMal.navn, "Navbruker må ha navn"); //$NON-NLS-1$
            return personinfoMal;
        }

    }

}

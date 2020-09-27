package no.nav.foreldrepenger.melding.aktør;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.PersonstatusType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

public class Personinfo {
    private AktørId aktørId;
    private LocalDate dødsdato;

    //Brukt for mapping
    private String navn;
    private PersonstatusType personstatus;
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

    public PersonstatusType getPersonstatus() {
        return personstatus;
    }

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<aktørId=" + aktørId + ">"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static class Builder {
        private Personinfo personinfoMal;

        public Builder() {
            personinfoMal = new Personinfo();
        }

        public Builder medAktørId(AktørId aktørId) {
            personinfoMal.aktørId = aktørId;
            return this;
        }

        public Builder medNavn(String navn) {
            personinfoMal.navn = navn;
            return this;
        }

        /**
         * @deprecated Bruk {@link #medPersonIdent(PersonIdent)} i stedet!
         */
        @Deprecated
        public Builder medFnr(String fnr) {
            personinfoMal.personIdent = PersonIdent.fra(fnr);
            return this;
        }

        public Builder medPersonIdent(PersonIdent fnr) {
            personinfoMal.personIdent = fnr;
            return this;
        }

        public Builder medDødsdato(LocalDate dødsdato) {
            personinfoMal.dødsdato = dødsdato;
            return this;
        }

        public Builder medPersonstatusType(PersonstatusType personstatus) {
            personinfoMal.personstatus = personstatus;
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
            requireNonNull(personinfoMal.kjønn, "Navbruker må ha kjønn"); //$NON-NLS-1$
            return personinfoMal;
        }

    }

}

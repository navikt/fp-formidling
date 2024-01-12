package no.nav.foreldrepenger.fpformidling.domene.aktør;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.domene.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.domene.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.domene.typer.PersonIdent;

public class Personinfo {
    private AktørId aktørId;
    private LocalDate fødselsdato;
    private LocalDate dødsdato;

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

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public LocalDate getDødsdato() {
        return dødsdato;
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

        public Builder medDødsdato(LocalDate dødsdato) {
            personinfoMal.dødsdato = dødsdato;
            return this;
        }

        public Builder medFødselsdato(LocalDate dato) {
            personinfoMal.fødselsdato = dato;
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

package no.nav.foreldrepenger.melding.fagsak;

import no.nav.foreldrepenger.melding.aktør.NavBruker;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

public class Fagsak {
    private Long id;
    private RelasjonsRolleType brukerRolle;
    private FagsakYtelseType ytelseType;
    private Saksnummer saksnummer;
    private Personinfo personinfo;

    private NavBruker navBruker;

    private Fagsak(Builder builder) {
        id = builder.id;
        brukerRolle = builder.brukerRolle;
        ytelseType = builder.ytelseType;
        saksnummer = builder.saksnummer;
        navBruker = builder.navBruker;
        personinfo = builder.personinfo;
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public Object getNavBruker() {
        return navBruker;
    }

    public RelasjonsRolleType getRelasjonsRolleType() {
        return brukerRolle;
    }

    public Personinfo getPersoninfo() {
        return personinfo;
    }

    public static final class Builder {
        private Long id;
        private RelasjonsRolleType brukerRolle;
        private FagsakYtelseType ytelseType;
        private Saksnummer saksnummer;
        private NavBruker navBruker;
        private Personinfo personinfo;

        private Builder() {
        }

        public Builder medId(Long id) {
            this.id = id;
            return this;
        }

        public Builder medBrukerRolle(RelasjonsRolleType brukerRolle) {
            this.brukerRolle = brukerRolle;
            return this;
        }

        public Builder medYtelseType(FagsakYtelseType ytelseType) {
            this.ytelseType = ytelseType;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            this.saksnummer = new Saksnummer(saksnummer);
            return this;
        }

        public Builder medNavBruker(NavBruker navBruker) {
            this.navBruker = navBruker;
            return this;
        }

        public Builder medPersoninfo(Personinfo personinfo) {
            this.personinfo = personinfo;
            return this;
        }

        public Fagsak build() {
            return new Fagsak(this);
        }
    }
}

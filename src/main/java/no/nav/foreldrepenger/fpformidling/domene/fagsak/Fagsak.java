package no.nav.foreldrepenger.fpformidling.domene.fagsak;

import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;

public class Fagsak {

    private AktørId aktørId;
    private Saksnummer saksnummer;
    private FagsakYtelseType ytelseType;
    private RelasjonsRolleType brukerRolle;
    private Integer dekningsgrad;

    private Fagsak() {
    }

    public static Builder ny() {
        return new Builder();
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    public RelasjonsRolleType getRelasjonsRolleType() {
        return brukerRolle;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public Integer getDekningsgrad() {
        return dekningsgrad;
    }

    public static final class Builder {
        private Fagsak kladd;

        private Builder() {
            this.kladd = new Fagsak();
        }

        public Builder medBrukerRolle(RelasjonsRolleType brukerRolle) {
            this.kladd.brukerRolle = brukerRolle;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            this.kladd.saksnummer = new Saksnummer(saksnummer);
            return this;
        }

        public Builder medYtelseType(FagsakYtelseType ytelseType) {
            this.kladd.ytelseType = ytelseType;
            return this;
        }

        public Builder medAktørId(AktørId aktørId) {
            this.kladd.aktørId = aktørId;
            return this;
        }

        public Builder medDekningsgrad(Integer dekningsgrad) {
            this.kladd.dekningsgrad = dekningsgrad;
            return this;
        }

        public Fagsak build() {
            return this.kladd;
        }
    }
}

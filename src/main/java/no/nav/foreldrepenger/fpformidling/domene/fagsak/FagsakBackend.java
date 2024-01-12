package no.nav.foreldrepenger.fpformidling.domene.fagsak;

import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.domene.typer.Saksnummer;

public class FagsakBackend {

    private AktørId aktørId;
    private Saksnummer saksnummer;
    private FagsakYtelseType ytelseType;
    private RelasjonsRolleType brukerRolle;
    private Integer dekningsgrad;

    private FagsakBackend() {
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
        private FagsakBackend kladd;

        private Builder() {
            this.kladd = new FagsakBackend();
        }

        public Builder medBrukerRolle(RelasjonsRolleType brukerRolle) {
            this.kladd.brukerRolle = brukerRolle;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            this.kladd.saksnummer = new Saksnummer(saksnummer);
            return this;
        }

        public Builder medFagsakYtelseType(FagsakYtelseType ytelseType) {
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

        public FagsakBackend build() {
            return this.kladd;
        }
    }
}

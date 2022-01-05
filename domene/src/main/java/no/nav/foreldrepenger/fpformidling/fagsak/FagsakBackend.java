package no.nav.foreldrepenger.fpformidling.fagsak;

import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;

public class FagsakBackend {

    private RelasjonsRolleType brukerRolle;
    private Saksnummer saksnummer;
    private AktørId aktørId;

    private FagsakBackend() {
    }

    public static Builder ny() {
        return new Builder();
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public RelasjonsRolleType getRelasjonsRolleType() {
        return brukerRolle;
    }

    public AktørId getAktørId() {
        return aktørId;
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

        public Builder medAktørId(AktørId aktørId) {
            this.kladd.aktørId = aktørId;
            return this;
        }

        public FagsakBackend build() {
            return this.kladd;
        }
    }
}

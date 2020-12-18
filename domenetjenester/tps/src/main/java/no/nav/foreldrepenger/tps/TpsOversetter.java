package no.nav.foreldrepenger.tps;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;

@ApplicationScoped
public class TpsOversetter {

    private TpsAdresseOversetter tpsAdresseOversetter;

    TpsOversetter() {
        // for CDI proxy
    }

    @Inject
    public TpsOversetter(TpsAdresseOversetter tpsAdresseOversetter) {
        this.tpsAdresseOversetter = tpsAdresseOversetter;
    }

    public Adresseinfo tilAdresseInfo(Person person) {
        return tpsAdresseOversetter.tilAdresseInfo(person);
    }

}

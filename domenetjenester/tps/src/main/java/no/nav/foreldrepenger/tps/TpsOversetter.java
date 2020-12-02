package no.nav.foreldrepenger.tps;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;

@ApplicationScoped
public class TpsOversetter {
    private static final Logger log = LoggerFactory.getLogger(TpsOversetter.class);


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

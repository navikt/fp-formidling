package no.nav.foreldrepenger.tps;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.felles.integrasjon.person.PersonConsumer;

@ApplicationScoped
public class TpsAdapter {
    private PersonConsumer personConsumer;
    private TpsOversetter tpsOversetter;

    public TpsAdapter() {
    }

    @Inject
    public TpsAdapter(PersonConsumer personConsumer,
                      TpsOversetter tpsOversetter) {
        this.personConsumer = personConsumer;
        this.tpsOversetter = tpsOversetter;
    }

    public Adresseinfo hentAdresseinformasjon(PersonIdent personIdent) {
        HentPersonRequest request = new HentPersonRequest();
        request.getInformasjonsbehov().add(Informasjonsbehov.ADRESSE);
        request.setAktoer(TpsUtil.lagPersonIdent(personIdent.getIdent()));
        try {
            HentPersonResponse response = personConsumer.hentPersonResponse(request);
            Person person = response.getPerson();
            return tpsOversetter.tilAdresseInfo(person);
        } catch (HentPersonPersonIkkeFunnet e) {
            throw TpsFeilmeldinger.FACTORY.fantIkkePerson(e).toException();
        } catch (HentPersonSikkerhetsbegrensning e) {
            throw TpsFeilmeldinger.FACTORY.tpsUtilgjengeligSikkerhetsbegrensning(e).toException();
        }
    }
}

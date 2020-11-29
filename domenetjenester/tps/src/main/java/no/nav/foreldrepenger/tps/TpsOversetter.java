package no.nav.foreldrepenger.tps;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Doedsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoenn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.vedtak.felles.integrasjon.felles.ws.DateUtil;

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

    public Personinfo tilBrukerInfo(AktørId aktørId, Bruker bruker) { // NOSONAR - ingen forbedring å forkorte metoden her
        String navn = bruker.getPersonnavn().getSammensattNavn();

        LocalDate dødsdato = finnDødsdato(bruker);
        LocalDate fødselsdato = finnFødselsdato(bruker);

        Aktoer aktoer = bruker.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        String ident = pi.getIdent().getIdent();
        NavBrukerKjønn kjønn = tilBrukerKjønn(bruker.getKjoenn());
        boolean registrertDød = TpsUtil.harPersonstatusDød(bruker);

        return Personinfo.getbuilder(aktørId)
                .medPersonIdent(no.nav.foreldrepenger.melding.typer.PersonIdent.fra(ident))
                .medNavn(navn)
                .medDødsdato(dødsdato)
                .medFødselsdato(fødselsdato)
                .medNavBrukerKjønn(kjønn)
                .medRegistrertDød(registrertDød)
                .build();
    }

    private LocalDate finnDødsdato(Bruker person) {
        LocalDate dødsdato = null;
        Doedsdato dødsdatoJaxb = person.getDoedsdato();
        if (dødsdatoJaxb != null) {
            dødsdato = DateUtil.convertToLocalDate(dødsdatoJaxb.getDoedsdato());
        }
        return dødsdato;
    }

    private LocalDate finnFødselsdato(Bruker person) {
        LocalDate fødselsdato = null;
        Foedselsdato fødselsdatoJaxb = person.getFoedselsdato();
        if (fødselsdatoJaxb != null) {
            fødselsdato = DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return fødselsdato;
    }

    private NavBrukerKjønn tilBrukerKjønn(Kjoenn kjoenn) {
        return Optional.ofNullable(kjoenn)
                .map(Kjoenn::getKjoenn)
                .map(kj -> NavBrukerKjønn.fraKode(kj.getValue()))
                .orElse(NavBrukerKjønn.UDEFINERT);
    }



}

package no.nav.foreldrepenger.tps.impl;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.tps.TpsAdapter;
import no.nav.foreldrepenger.tps.TpsTjeneste;

@ApplicationScoped
public class TpsTjenesteImpl implements TpsTjeneste {

    private TpsAdapter tpsAdapter;

    public TpsTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public TpsTjenesteImpl(TpsAdapter tpsAdapter) {
        this.tpsAdapter = tpsAdapter;
    }

    @Override
    public Optional<PersonIdent> hentFnr(AktørId aktørId) {
        return tpsAdapter.hentIdentForAktørId(aktørId);
    }

    @Override
    public Optional<Personinfo> hentBrukerForAktør(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = hentFnr(aktørId);
        return funnetFnr.map(fnr -> tpsAdapter.hentKjerneinformasjon(fnr, aktørId));
    }

    @Override
    public Adresseinfo hentAdresseinformasjon(PersonIdent personIdent) {
        return tpsAdapter.hentAdresseinformasjon(personIdent);
    }

    @Override
    public Optional<Personinfo> hentBrukerForFnr(PersonIdent fnr) {
        if (fnr.erFdatNummer()) {
            return Optional.empty();
        }
        Optional<AktørId> aktørId = tpsAdapter.hentAktørIdForPersonIdent(fnr);
        if (!aktørId.isPresent()) {
            return Optional.empty();
        }
        try {
            Personinfo personinfo = tpsAdapter.hentKjerneinformasjon(fnr, aktørId.get());
            return Optional.ofNullable(personinfo);
        } catch (SOAPFaultException e) {
            if (e.getMessage().contains("status: S100008F")) {
                // Her sorterer vi ut dødfødte barn
                return Optional.empty();
            } else {
                throw e;
            }
        }
    }

    @Override
    public Optional<AktørId> hentAktørForFnr(PersonIdent fnr) {
        return tpsAdapter.hentAktørIdForPersonIdent(fnr);
    }
}

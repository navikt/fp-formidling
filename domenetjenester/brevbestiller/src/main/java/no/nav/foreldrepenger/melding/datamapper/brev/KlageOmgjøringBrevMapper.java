package no.nav.foreldrepenger.melding.datamapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_OMGJØRING)
public class KlageOmgjøringBrevMapper extends FritekstmalBrevMapper {

    public KlageOmgjøringBrevMapper() {
        //CDI
    }

    @Inject
    public KlageOmgjøringBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om omgjøring av klage";
    }

    @Override
    public String templateFolder() {
        return "vedtakomomgjoringavklage";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {

        Klage klage  = domeneobjektProvider.hentKlagebehandling(behandling);
        if( klage !=null) {
            return new Brevdata()
                    .leggTil("mintekst",  klage.getGjeldendeKlageVurderingsresultat().getFritekstTilBrev())
                    .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                    .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                    .leggTil("behandlingtype",behandling.getBehandlingType().getKode());
        }
        return new Brevdata()
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype",behandling.getBehandlingType().getKode());

    }
}




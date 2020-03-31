package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;

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
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        initHandlebars(behandling.getSpråkkode());

        Map<String, Object> hovedoverskriftFelter = new HashMap<>();
        hovedoverskriftFelter.put("behandling", behandling);
        hovedoverskriftFelter.put("dokumentHendelse", hendelse);
        hovedoverskriftFelter.put("behandlesAvKlageinstans", behandlesAvKlageinstans(hendelse, behandling));
        Map<String, Object> brødtekstFelter = mapTilBrevfelter(hendelse, behandling).getMap();

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        if (klage != null) {
            hovedoverskriftFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
            brødtekstFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
            if (hendelse.getFritekst() != null) {
                brødtekstFelter.put("mintekst", hendelse.getFritekst());
            } else if (klage.getGjeldendeKlageVurderingsresultat() != null) {
                brødtekstFelter.put("mintekst", klage.getGjeldendeKlageVurderingsresultat().getFritekstTilBrev());
            }
        }

        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(hovedoverskriftFelter, getOverskriftMal()));
        fagType.setBrødtekst(tryApply(brødtekstFelter, getBrødtekstMal()));
        return fagType;
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        return new Brevdata()
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver", behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype", behandling.getBehandlingType().getKode())
                .leggTil("behandlesAvKlageinstans", behandlesAvKlageinstans(hendelse, behandling));
    }
}




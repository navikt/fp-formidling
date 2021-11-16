package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst.fra;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@Named(DokumentMalTypeKode.KLAGE_STADFESTET_FRITEKST)
public class KlageStadfestelseBrevMapper extends FritekstmalBrevMapper {

    public KlageStadfestelseBrevMapper() {
        //CDI
    }

    @Inject
    public KlageStadfestelseBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om stadfestelse i klagesak";
    }

    @Override
    public String templateFolder() {
        return "vedtakomstadfestelseiklagesak";
    }

    @Override
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        initHandlebars(behandling.getSpråkkode());

        Map<String, Object> hovedoverskriftFelter = new HashMap<>();
        hovedoverskriftFelter.put("behandling", behandling);
        hovedoverskriftFelter.put("dokumentHendelse", hendelse);

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        if (klage != null) {
            hovedoverskriftFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
        }

        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(hovedoverskriftFelter, getOverskriftMal()));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling).getMap(), getBrødtekstMal()));
        return fagType;
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {

        Brevdata brevdata = new Brevdata()
                .leggTil("ytelseType", hendelse.getYtelseType().getKode());

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        Optional<Fritekst> fritekstOpt = fra(hendelse, klage);
        if (fritekstOpt.isPresent()) {
            brevdata.leggTil("mintekst", fritekstOpt.get().getFritekst());
        }
        return brevdata;
    }
}

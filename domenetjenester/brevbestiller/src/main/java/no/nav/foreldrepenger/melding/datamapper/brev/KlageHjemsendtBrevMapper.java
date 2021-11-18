package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst.fra;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@Named(DokumentMalTypeKode.KLAGE_HJEMSENDT_FRITEKST)
public class KlageHjemsendtBrevMapper extends FritekstmalBrevMapper {

    private BrevMapperUtil brevMapperUtil;

    public KlageHjemsendtBrevMapper() {
        //CDI
    }

    @Inject
    public KlageHjemsendtBrevMapper(BrevParametere brevParametere,
                                    DomeneobjektProvider domeneobjektProvider,
                                    BrevMapperUtil brevMapperUtil) {
        super(brevParametere, domeneobjektProvider);
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String displayName() {
        return "Vedtak om hjemsending og evt. oppheving i klagesak";
    }

    @Override
    public String templateFolder() {
        return "vedtakomhjemsendingiklagesak";
    }

    @Override
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        initHandlebars(behandling.getSpråkkode());

        Map<String, Object> hovedoverskriftFelter = new HashMap<>();
        hovedoverskriftFelter.put("behandling", behandling);
        hovedoverskriftFelter.put("dokumentHendelse", hendelse);

        Map<String, Object> brødtekstFelter = mapTilBrevfelter(hendelse, behandling).getMap();

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        if (klage != null) {
            hovedoverskriftFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
            brødtekstFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
        }

        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(hovedoverskriftFelter, getOverskriftMal()));
        fagType.setBrødtekst(tryApply(brødtekstFelter, getBrødtekstMal()));
        return fagType;
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Brevdata brevdata = new Brevdata();
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        Optional<Fritekst> fritekstOpt = fra(hendelse, klage);
        fritekstOpt.ifPresent(s -> brevdata.leggTil("mintekst", s.getFritekst()));

        brevdata.leggTil("ytelseType", hendelse.getYtelseType().getKode());
        brevdata.leggTil("opphevet", KlageMapper.erOpphevet(klage, hendelse));
        brevdata.leggTil("ettersendelsesfrist", formaterDatoNorsk(brevMapperUtil.getSvarFrist()));

        return brevdata;
    }
}

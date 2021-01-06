package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@Named(DokumentMalTypeKode.KLAGE_OVERSENDT_KLAGEINSTANS)
public class KlageOversendtTilKlageinstansBrevMapper extends FritekstmalBrevMapper {

    public KlageOversendtTilKlageinstansBrevMapper() {
        //CDI
    }

    @Inject
    public KlageOversendtTilKlageinstansBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Klage oversendt til klageinstans";
    }

    @Override
    public String templateFolder() {
        return "klageoversendtklageinstans";
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
            brødtekstFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
        }

        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(hovedoverskriftFelter, getOverskriftMal()));
        fagType.setBrødtekst(tryApply(brødtekstFelter, getBrødtekstMal()));
        return fagType;
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        KlageDokument klageDokument = domeneobjektProvider.hentKlageDokument(behandling);
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        LocalDate mottatDato = utledMottattDato(klageDokument, behandling);

        Brevdata brevdata = new Brevdata()
                .leggTil("mottatDato", formaterDatoNorsk(mottatDato))
                .leggTil("dokumentHendelseYtelseTypeKode", hendelse.getYtelseType().getKode());

        if (hendelse.getFritekst() != null) { // Forhåndsvisning
            brevdata.leggTil("mintekst", hendelse.getFritekst());
        } else if (klage.getGjeldendeKlageVurderingsresultat() != null) { // Bestilling
            brevdata.leggTil("mintekst", klage.getGjeldendeKlageVurderingsresultat().getFritekstTilBrev());
        }
        return brevdata;
    }

    private LocalDate utledMottattDato(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.getMottattDato() != null
                ? klageDokument.getMottattDato()
                : behandling.getOpprettetDato().toLocalDate();
    }
}




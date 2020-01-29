package no.nav.foreldrepenger.melding.datamapper.brev;

import java.time.LocalDate;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_OVERSENDT_KLAGEINSTANS)
public class KlageOversendtTilKlageinstansBrevMapper extends FritekstmalBrevMapper {

    private static final int BEHANDLINGSFRIST_UKER_KA = 14;

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
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {

        KlageDokument klageDokument = domeneobjektProvider.hentKlageDokument(behandling);
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        final LocalDate mottatDato = utledMottattDato(klageDokument, behandling);
        final LocalDate svarFrist = BrevMapperUtil.getSvarFrist(brevParametere);

        return new Brevdata()
                .leggTil("mottatDato", mottatDato)
                .leggTil("mintekst",  klage.getGjeldendeKlageVurderingsresultat().getFritekstTilBrev())
                .leggTil("behandlingsfrist", BEHANDLINGSFRIST_UKER_KA)
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype",behandling.getBehandlingType().getKode());

    }
    private LocalDate utledMottattDato(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.getMottattDato() != null
                ? klageDokument.getMottattDato()
                : behandling.getOpprettetDato().toLocalDate();
    }
}




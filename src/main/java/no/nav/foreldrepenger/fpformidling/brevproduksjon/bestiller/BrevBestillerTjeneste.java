package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.BestillingType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ApplicationScoped
public class BrevBestillerTjeneste {

    private DomeneobjektProvider domeneobjektProvider;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    BrevBestillerTjeneste() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerTjeneste(DomeneobjektProvider domeneobjektProvider,
                                 DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokgenBrevproduksjonTjeneste = dokgenBrevproduksjonTjeneste;
    }

    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse) {
        var behandling = hentBehandling(dokumentHendelse.getBehandlingUuid());
        return dokgenBrevproduksjonTjeneste.forhåndsvisBrev(dokumentHendelse, behandling);
    }

    public String genererBrevHtml(DokumentHendelse dokumentHendelse) {
        var behandling = hentBehandling(dokumentHendelse.getBehandlingUuid());
        return dokgenBrevproduksjonTjeneste.genererBrevHtml(dokumentHendelse, behandling);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        var behandling = hentBehandling(dokumentHendelse.getBehandlingUuid());
        var journalførSom = utledDokumentType(dokumentHendelse);
        dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, journalførSom);
    }

    public String genererJson(UUID behandlingUuid, DokumentMal dokumentMal){
        var behandling = hentBehandling(behandlingUuid);
        var dokumentHendelse = DokumentHendelse.builder()
            .medBestillingUuid(UUID.randomUUID())
            .medBehandlingUuid(behandlingUuid)
            .medDokumentMal(dokumentMal).build();
        return dokgenBrevproduksjonTjeneste.genererJson(dokumentHendelse, behandling, BestillingType.UTKAST);
    }

    private DokumentMalType utledDokumentType(DokumentHendelse dokumentHendelse) {
        var dokumentMal = dokumentHendelse.getDokumentMal();
        if (DokumentMal.FRITEKSTBREV.equals(dokumentMal) || DokumentMal.FRITEKSTBREV_HTML.equals(dokumentMal)) {
            return mapDokumentMalType(dokumentHendelse.getJournalførSom());
        }
        return mapDokumentMalType(dokumentMal);
    }

    private DokumentMalType mapDokumentMalType(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FRITEKSTBREV -> DokumentMalType.FRITEKSTBREV;
            case FRITEKSTBREV_HTML -> DokumentMalType.FRITEKSTBREV_HTML;
            case KLAGE_AVVIST -> DokumentMalType.KLAGE_AVVIST;
            case KLAGE_OMGJORT -> DokumentMalType.KLAGE_OMGJORT;
            case KLAGE_OVERSENDT -> DokumentMalType.KLAGE_OVERSENDT;
            case IKKE_SØKT -> DokumentMalType.IKKE_SØKT;
            case INGEN_ENDRING -> DokumentMalType.INGEN_ENDRING;
            case INNSYN_SVAR -> DokumentMalType.INNSYN_SVAR;
            case INFO_OM_HENLEGGELSE -> DokumentMalType.INFO_OM_HENLEGGELSE;
            case VARSEL_OM_REVURDERING -> DokumentMalType.VARSEL_OM_REVURDERING;
            case INNHENTE_OPPLYSNINGER -> DokumentMalType.INNHENTE_OPPLYSNINGER;
            case ETTERLYS_INNTEKTSMELDING -> DokumentMalType.ETTERLYS_INNTEKTSMELDING;
            case ENGANGSSTØNAD_AVSLAG -> DokumentMalType.ENGANGSSTØNAD_AVSLAG;
            case ENGANGSSTØNAD_INNVILGELSE -> DokumentMalType.ENGANGSSTØNAD_INNVILGELSE;
            case FORELDREPENGER_OPPHØR -> DokumentMalType.FORELDREPENGER_OPPHØR;
            case FORELDREPENGER_AVSLAG -> DokumentMalType.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_ANNULLERT -> DokumentMalType.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INNVILGELSE -> DokumentMalType.FORELDREPENGER_INNVILGELSE;
            case SVANGERSKAPSPENGER_AVSLAG -> DokumentMalType.SVANGERSKAPSPENGER_AVSLAG;
            case SVANGERSKAPSPENGER_OPPHØR -> DokumentMalType.SVANGERSKAPSPENGER_OPPHØR;
            case SVANGERSKAPSPENGER_INNVILGELSE -> DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE;
            case FORLENGET_SAKSBEHANDLINGSTID -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG;
            case ENDRING_UTBETALING -> DokumentMalType.ENDRING_UTBETALING;
            case FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER -> DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV -> DokumentMalType.FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV;
            case FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_FORLENGET_SAKSBEHANDLINGSTID -> DokumentMalType.FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_FORLENGET_SAKSBEHANDLINGSTID;
            case null -> throw new NullPointerException("Ugyldig dokument mal type.");
        };
    }

    private Behandling hentBehandling(UUID behandlingUuid) {
        var behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        domeneobjektProvider.hentFagsakBackend(behandling);
        return behandling;
    }

}

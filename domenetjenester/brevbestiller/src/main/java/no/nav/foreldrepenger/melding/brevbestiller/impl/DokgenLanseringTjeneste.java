package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokgenLanseringTjeneste {

    DokgenLanseringTjeneste() {
        // CDI
    }

    private static final Environment ENV = Environment.current();
    private static final Set<DokumentMalType> DOKGEN_MALER_PROD = Set.of(
            DokumentMalType.ENGANGSSTØNAD_INNVILGELSE,
            DokumentMalType.ENGANGSSTØNAD_AVSLAG,
            DokumentMalType.IKKE_SØKT,
            DokumentMalType.INNHENTE_OPPLYSNINGER,
            DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INGEN_ENDRING,
            DokumentMalType.INNSYN_SVAR,
            DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG,
            DokumentMalType.FORELDREPENGER_INNVILGELSE,
            DokumentMalType.FORELDREPENGER_AVSLAG,
            DokumentMalType.KLAGE_AVVIST,
            DokumentMalType.KLAGE_HJEMSENDT,
            DokumentMalType.KLAGE_OMGJORT,
            DokumentMalType.KLAGE_OVERSENDT,
            DokumentMalType.KLAGE_STADFESTET);
    private static final Set<DokumentMalType> DOKGEN_MALER_DEV = Set.of(
            DokumentMalType.ENGANGSSTØNAD_INNVILGELSE,
            DokumentMalType.ENGANGSSTØNAD_AVSLAG,
            DokumentMalType.IKKE_SØKT,
            DokumentMalType.INNHENTE_OPPLYSNINGER,
            DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INGEN_ENDRING,
            DokumentMalType.INNSYN_SVAR,
            DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG,
            DokumentMalType.FORELDREPENGER_INNVILGELSE,
            DokumentMalType.FORELDREPENGER_AVSLAG,
            DokumentMalType.KLAGE_AVVIST,
            DokumentMalType.KLAGE_HJEMSENDT,
            DokumentMalType.KLAGE_OMGJORT,
            DokumentMalType.KLAGE_OVERSENDT,
            DokumentMalType.KLAGE_STADFESTET,
            DokumentMalType.FORELDREPENGER_ANNULLERT,
            DokumentMalType.FORELDREPENGER_OPPHØR);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_PROD = Set.of(
            DokumentMalType.INNHENTE_OPPLYSNINGER_DOK,
            DokumentMalType.VARSEL_OM_REVURDERING_DOK,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_DOK,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_DEV = Set.of(
            DokumentMalType.INNHENTE_OPPLYSNINGER_DOK,
            DokumentMalType.VARSEL_OM_REVURDERING_DOK,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_DOK,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK);
    private static final Map<DokumentMalType, DokumentMalType> OVERSTYRE_MAL_DEV = Map.of(
            DokumentMalType.VARSEL_OM_REVURDERING_DOK, DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.INFO_OM_HENLEGGELSE_DOK, DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INNSYN_SVAR_DOK, DokumentMalType.INNSYN_SVAR,
            DokumentMalType.KLAGE_HJEMSENDT_FRITEKST, DokumentMalType.KLAGE_HJEMSENDT,
            DokumentMalType.KLAGE_OMGJORT_FRITEKST, DokumentMalType.KLAGE_OMGJORT,
            DokumentMalType.KLAGE_OVERSENDT_FRITEKST, DokumentMalType.KLAGE_OVERSENDT,
            DokumentMalType.KLAGE_STADFESTET_FRITEKST, DokumentMalType.KLAGE_STADFESTET);
    private static final Map<DokumentMalType, DokumentMalType> OVERSTYRE_MAL_PROD = Map.of(
            DokumentMalType.VARSEL_OM_REVURDERING_DOK, DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.INFO_OM_HENLEGGELSE_DOK, DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INNSYN_SVAR_DOK, DokumentMalType.INNSYN_SVAR,
            DokumentMalType.KLAGE_HJEMSENDT_FRITEKST, DokumentMalType.KLAGE_HJEMSENDT,
            DokumentMalType.KLAGE_OMGJORT_FRITEKST, DokumentMalType.KLAGE_OMGJORT,
            DokumentMalType.KLAGE_OVERSENDT_FRITEKST, DokumentMalType.KLAGE_OVERSENDT,
            DokumentMalType.KLAGE_STADFESTET_FRITEKST, DokumentMalType.KLAGE_STADFESTET);

    public static boolean malSkalBrukeDokgen(DokumentMalType dokumentMalType) {
        if (ENV.isProd()) {
            return DOKGEN_MALER_PROD.contains(dokumentMalType);
        } else {
            return DOKGEN_MALER_DEV.contains(dokumentMalType);
        }
    }

    public static boolean malSkalIkkeTilgjengeliggjøresForManuellUtsendelse(DokumentMalType dokumentMalType) {
        if (ENV.isProd()) {
            return SKJULTE_MANUELLE_MALER_PROD.contains(dokumentMalType);
        } else {
            return SKJULTE_MANUELLE_MALER_DEV.contains(dokumentMalType);
        }
    }

    public static DokumentMalType overstyrMalHvisNødvendig(DokumentMalType dokumentMalType) {
        if (ENV.isProd() && OVERSTYRE_MAL_PROD.containsKey(dokumentMalType)) {
            return OVERSTYRE_MAL_PROD.get(dokumentMalType);
        } else if (!ENV.isProd() && OVERSTYRE_MAL_DEV.containsKey(dokumentMalType)) {
            return OVERSTYRE_MAL_DEV.get(dokumentMalType);
        }
        return dokumentMalType;
    }
}

package no.nav.foreldrepenger.melding.brevbestiller.impl;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class DokgenLanseringTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenLanseringTjeneste.class);

    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public DokgenLanseringTjeneste(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

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
            DokumentMalType.FORELDREPENGER_AVSLAG);
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
            DokumentMalType.FORELDREPENGER_AVSLAG);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_PROD = Set.of(
            DokumentMalType.INNHENT_DOK,
            DokumentMalType.REVURDERING_DOK,
            DokumentMalType.FORLENGET_DOK,
            DokumentMalType.FORLENGET_MEDL_DOK);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_DEV = Set.of(
            DokumentMalType.INNHENT_DOK,
            DokumentMalType.REVURDERING_DOK,
            DokumentMalType.FORLENGET_DOK,
            DokumentMalType.FORLENGET_MEDL_DOK);
    private static final Map<DokumentMalType, DokumentMalType> OVERSTYRE_MAL_DEV = Map.of(
            DokumentMalType.REVURDERING_DOK, DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.HENLEGG_BEHANDLING_DOK, DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INNSYNSKRAV_SVAR, DokumentMalType.INNSYN_SVAR);
    private static final Map<DokumentMalType, DokumentMalType> OVERSTYRE_MAL_PROD = Map.of(
            DokumentMalType.REVURDERING_DOK, DokumentMalType.VARSEL_OM_REVURDERING,
            DokumentMalType.HENLEGG_BEHANDLING_DOK, DokumentMalType.INFO_OM_HENLEGGELSE,
            DokumentMalType.INNSYNSKRAV_SVAR, DokumentMalType.INNSYN_SVAR);

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

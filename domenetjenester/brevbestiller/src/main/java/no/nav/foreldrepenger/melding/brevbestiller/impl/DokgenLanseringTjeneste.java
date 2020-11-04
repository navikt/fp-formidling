package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Set;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.util.env.Environment;

public class DokgenLanseringTjeneste {

    private static final Environment ENV = Environment.current();
    private static final Set<DokumentMalType> DOKGEN_MALER_PROD = Set.of();
    private static final Set<DokumentMalType> DOKGEN_MALER_DEV = Set.of(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD,
            DokumentMalType.INNHENTE_OPPLYSNINGER);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_PROD = Set.of(DokumentMalType.INNHENTE_OPPLYSNINGER);
    private static final Set<DokumentMalType> SKJULTE_MANUELLE_MALER_DEV = Set.of(DokumentMalType.INNHENT_DOK);

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
}

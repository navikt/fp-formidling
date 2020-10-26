package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Set;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.util.env.Environment;

public class DokgenLanseringTjeneste {

    private static final Environment ENV = Environment.current();
    private static final Set<String> DOKGEN_MALER_PROD = Set.of();
    private static final Set<String> DOKGEN_MALER_DEV = Set.of(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD.getKode());

    public static boolean malSkalBrukeDokgen(DokumentMalType dokumentMalType) {
        if (ENV.isProd()) {
            return DOKGEN_MALER_PROD.contains(dokumentMalType.getKode());
        } else {
            return DOKGEN_MALER_DEV.contains(dokumentMalType.getKode());
        }
    }
}

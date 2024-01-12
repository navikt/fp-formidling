package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.DokumentMalType;

class DistribusjonstypeUtleder {

    private DistribusjonstypeUtleder() {
    }

    static Distribusjonstype utledFor(DokumentMalType dokumentMal) {
        if (DokumentMalType.erVedtaksBrev(dokumentMal) || DokumentMalType.FRITEKSTBREV.equals(dokumentMal)) {
            return Distribusjonstype.VEDTAK;
        } else if (DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_BREVMALER.contains(dokumentMal)
            || DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER.equals(dokumentMal)) {
            return Distribusjonstype.ANNET;
        }
        return Distribusjonstype.VIKTIG;
    }
}

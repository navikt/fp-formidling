package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DistribusjonstypeUtleder.utledFor;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype.ANNET;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype.VEDTAK;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype.VIKTIG;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

class DistribusjonstypeUtlederTest {

    @Test
    void utledDistribusjonstypeVedtakTest() {
        assertThat(utledFor(DokumentMalType.ENGANGSSTØNAD_AVSLAG)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.FORELDREPENGER_AVSLAG)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.FORELDREPENGER_INNVILGELSE)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.FORELDREPENGER_OPPHØR)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.FORELDREPENGER_ANNULLERT)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.SVANGERSKAPSPENGER_AVSLAG)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.SVANGERSKAPSPENGER_OPPHØR)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.KLAGE_AVVIST)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.KLAGE_HJEMSENDT)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.KLAGE_OMGJORT)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.KLAGE_STADFESTET)).isEqualTo(VEDTAK);
        assertThat(utledFor(DokumentMalType.FRITEKSTBREV)).isEqualTo(VEDTAK);
    }

    @Test
    void utledViktigTest() {
        assertThat(utledFor(DokumentMalType.KLAGE_OVERSENDT)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.INGEN_ENDRING)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.INFO_OM_HENLEGGELSE)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.INNHENTE_OPPLYSNINGER)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.INNSYN_SVAR)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.ETTERLYS_INNTEKTSMELDING)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.VARSEL_OM_REVURDERING)).isEqualTo(VIKTIG);
        assertThat(utledFor(DokumentMalType.IKKE_SØKT)).isEqualTo(VIKTIG);
    }

    @Test
    void utledAnnetTest() {
        assertThat(utledFor(DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER)).isEqualTo(ANNET);
        assertThat(utledFor(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID)).isEqualTo(ANNET);
        assertThat(utledFor(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL)).isEqualTo(ANNET);
        assertThat(utledFor(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG)).isEqualTo(ANNET);
    }
}

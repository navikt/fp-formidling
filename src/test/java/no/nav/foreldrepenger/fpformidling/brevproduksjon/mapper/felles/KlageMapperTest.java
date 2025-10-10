package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling.KlageAvvistÅrsak;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling.KlageFormkravResultat;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling;

class KlageMapperTest {

    @Test
    void formaterLovhjemlerKlageAvvistTest() {
        var klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.IKKE_KONKRET));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), "forvaltningsloven §§ 31, 32 og 33");

        klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGER_IKKE_PART));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), "forvaltningsloven §§ 28 og 33");
    }

    private KlageBehandling lagKlageNFP(List<KlageAvvistÅrsak> avvistÅrsaker) {
        return new KlageBehandling(
            new KlageFormkravResultat(1L, UUID.randomUUID(), BehandlingType.FØRSTEGANGSSØKNAD, "begrunnelse", true, true, false,
                avvistÅrsaker.isEmpty(), avvistÅrsaker), null, null, null, List.of(), true, true, LocalDate.now());
    }

    private void assertLovFormateringKlage(Set<String> input, String forventetOutput) {
        var lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, false, Språkkode.NB).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}

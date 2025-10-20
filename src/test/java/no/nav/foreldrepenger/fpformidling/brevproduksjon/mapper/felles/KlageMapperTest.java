package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.KlageBehandling.KlageFormkravResultat;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.KlageBehandling;

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
            new KlageFormkravResultat(BehandlingType.FØRSTEGANGSSØKNAD, avvistÅrsaker), null, null, null, LocalDate.now());
    }

    private void assertLovFormateringKlage(Set<String> input, String forventetOutput) {
        var lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, false, Språkkode.NB).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}

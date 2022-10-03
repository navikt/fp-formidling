package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.KlageDtoMapper;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurdering;

public class KlageMapperTest {


    @Test
    public void formaterLovhjemlerKlageAvvistTest() {
        var klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.IKKE_KONKRET));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage),  "forvaltningsloven §§ 31, 32 og 33");

        klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGER_IKKE_PART));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage),  "forvaltningsloven §§ 28 og 33");
    }

    private Klage lagKlageNFP(List<KlageAvvistÅrsak> avvistÅrsaker) {
        return KlageDtoMapper.mapKlagefraDto(lagKlageDto(avvistÅrsaker));
    }

    private KlagebehandlingDto lagKlageDto(List<KlageAvvistÅrsak> avvistÅrsaker) {
        var dto = new KlagebehandlingDto();
        dto.setKlageVurderingResultatNFP(lagKlagevurderingResultatDto());
        dto.setKlageFormkravResultatNFP(lagFormkravResultatDto(avvistÅrsaker));
        return dto;
    }

    private KlageFormkravResultatDto lagFormkravResultatDto(List<KlageAvvistÅrsak> avvistÅrsaker) {
        var dto = new KlageFormkravResultatDto();
        dto.setAvvistArsaker(avvistÅrsaker);
        dto.setPaklagdBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD);
        return dto;
    }

    private KlageVurderingResultatDto lagKlagevurderingResultatDto() {
        var resultatDto = new KlageVurderingResultatDto();

        resultatDto.setKlageVurdering(KlageVurdering.AVVIS_KLAGE);

       return resultatDto;
    }

    private void assertLovFormateringKlage(Set<String> input,  String forventetOutput) {
        var lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, false, Språkkode.NB).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}

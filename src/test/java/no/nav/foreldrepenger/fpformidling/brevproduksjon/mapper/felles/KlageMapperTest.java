package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling.KlageAvvistÅrsak;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling.KlageFormkravResultat;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageVurdering;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.KlageBehandling;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlagebehandlingDto;

class KlageMapperTest {


    @Test
    void formaterLovhjemlerKlageAvvistTest() {
        var klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.IKKE_KONKRET));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), "forvaltningsloven §§ 31, 32 og 33");

        klage = lagKlageNFP(List.of(KlageAvvistÅrsak.KLAGER_IKKE_PART));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), "forvaltningsloven §§ 28 og 33");
    }

    private KlageBehandling lagKlageNFP(List<KlageAvvistÅrsak> avvistÅrsaker) {
        return new KlageBehandling(new KlageFormkravResultat(1L, UUID.randomUUID(), BrevGrunnlag.BehandlingType.FØRSTEGANGSSØKNAD,
            "begrunnelse", true, true, false, avvistÅrsaker.isEmpty(), avvistÅrsaker), null, null, null, List.of(), true, true, LocalDate.now());
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

    private void assertLovFormateringKlage(Set<String> input, String forventetOutput) {
        var lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, false, Språkkode.NB).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}

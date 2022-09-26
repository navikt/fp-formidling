package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.KlageDtoMapper;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurdering;

public class KlageMapperTest {

    private DokumentHendelse dokumentHendelse;

    @BeforeEach
    public void setup() {
        dokumentHendelse = new DokumentHendelse();
    }

    @Test
    public void skal_identifisere_opphevet_klage() {
        assertThat(KlageMapper.erOpphevet(lagKlageNFP(true, Collections.emptyList()), dokumentHendelse)).isTrue();
    }

    @Test
    public void skal_identifisere_opphevet_klage_basert_på_hendelse() {
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medErOpphevetKlage(true)
                .build();
        assertThat(KlageMapper.erOpphevet(lagKlageNFP(false, Collections.emptyList()), dokumentHendelse)).isTrue();
    }

    @Test
    public void skal_identifisere_ikke_opphevet_klage() {
        assertThat(KlageMapper.erOpphevet(lagKlageNFP(false, Collections.emptyList()), dokumentHendelse)).isFalse();
    }

    @Test
    public void formaterLovhjemlerKlageAvvistTest() {
        var klage = lagKlageNFP(false, List.of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.IKKE_KONKRET));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), false, "forvaltningsloven §§ 31, 32 og 33");

        klage = lagKlageNFP(false, List.of(KlageAvvistÅrsak.KLAGER_IKKE_PART));
        assertLovFormateringKlage(KlageMapper.hentKlageHjemler(klage), false, "forvaltningsloven §§ 28 og 33");
    }

    private Klage lagKlageNFP(boolean opphevet, List<KlageAvvistÅrsak> avvistÅrsaker) {
        return KlageDtoMapper.mapKlagefraDto(lagKlageDto(opphevet, avvistÅrsaker));
    }

    private KlagebehandlingDto lagKlageDto(boolean opphevet, List<KlageAvvistÅrsak> avvistÅrsaker) {
        var dto = new KlagebehandlingDto();
        dto.setKlageVurderingResultatNFP(lagKlagevurderingResultatDto(opphevet));
        dto.setKlageFormkravResultatNFP(lagFormkravResultatDto(avvistÅrsaker));
        return dto;
    }

    private KlageFormkravResultatDto lagFormkravResultatDto(List<KlageAvvistÅrsak> avvistÅrsaker) {
        var dto = new KlageFormkravResultatDto();
        dto.setAvvistArsaker(avvistÅrsaker);
        dto.setPaklagdBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD);
        return dto;
    }

    private KlageVurderingResultatDto lagKlagevurderingResultatDto(boolean opphevet) {
        var resultatDto = new KlageVurderingResultatDto();
        if (opphevet) {
            resultatDto.setKlageVurdering(KlageVurdering.OPPHEVE_YTELSESVEDTAK);
        } else {
            resultatDto.setKlageVurdering(KlageVurdering.AVVIS_KLAGE);
        }
        return resultatDto;
    }

    private void assertLovFormateringKlage(Set<String> input, boolean klagetEtterKlagefrist, String forventetOutput) {
        var lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, klagetEtterKlagefrist, Språkkode.NB).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}

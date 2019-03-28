package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;

public class KlageMapperTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KlageMapper klageMapper;
    private KodeverkRepository kodeverkRepository;

    @Before
    public void setup() {
        kodeverkRepository = new KodeverkRepositoryImpl(repositoryRule.getEntityManager());
        klageMapper = new KlageMapper(kodeverkRepository, null);
    }

    @Test
    public void skal_identifisere_opphevet_klage() {
        assertThat(klageMapper.erOpphevet(lagKlageNFP(true, Collections.emptyList()))).isTrue();
    }

    @Test
    public void skal_identifisere_ikke_opphevet_klage() {
        assertThat(klageMapper.erOpphevet(lagKlageNFP(false, Collections.emptyList()))).isFalse();
    }

    @Test
    public void formaterLovhjemlerKlageAvvistTest() throws IOException {

        Klage klage = lagKlageNFP(false, List.of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.IKKE_KONKRET));
        assertLovFormateringKlage(klageMapper.hentKlageHjemler(klage), false, "forvaltningsloven §§ 31, 32 og 33");

        klage = lagKlageNFP(false, List.of(KlageAvvistÅrsak.KLAGER_IKKE_PART));
        assertLovFormateringKlage(klageMapper.hentKlageHjemler(klage), false, "forvaltningsloven §§ 28 og 33");
    }

    private Klage lagKlageNFP(boolean opphevet, List<KlageAvvistÅrsak> avvistÅrsaker) {
        return Klage.fraDto(lagKlageDto(opphevet, avvistÅrsaker), kodeverkRepository);
    }

    private KlagebehandlingDto lagKlageDto(boolean opphevet, List<KlageAvvistÅrsak> avvistÅrsaker) {
        KlagebehandlingDto dto = new KlagebehandlingDto();
        dto.setKlageVurderingResultatNFP(lagKlagevurderingResultatDto(opphevet));
        dto.setKlageFormkravResultatNFP(lagFormkravResultatDto(avvistÅrsaker));
        return dto;
    }

    private KlageFormkravResultatDto lagFormkravResultatDto(List<KlageAvvistÅrsak> avvistÅrsaker) {
        KlageFormkravResultatDto dto = new KlageFormkravResultatDto();
        dto.setAvvistArsaker(kodeverkTilDto(avvistÅrsaker));
        return dto;
    }

    private List<KodeDto> kodeverkTilDto(List<? extends Kodeliste> kodeliste) {
        List<KodeDto> dtoListe = new ArrayList<>();
        kodeliste.forEach(k -> dtoListe.add(new KodeDto(k.getKodeverk(), k.getKode(), k.getNavn())));
        return dtoListe;
    }

    private KlageVurderingResultatDto lagKlagevurderingResultatDto(boolean opphevet) {
        KlageVurderingResultatDto resultatDto = new KlageVurderingResultatDto();
        if (opphevet) {
            resultatDto.setKlageVurdering(KlageVurdering.OPPHEVE_YTELSESVEDTAK.getKode());
        } else {
            resultatDto.setKlageVurdering(KlageVurdering.AVVIS_KLAGE.getKode());
        }
        return resultatDto;
    }

    private void assertLovFormateringKlage(Set<String> input, boolean klagetEtterKlagefrist, String forventetOutput) {
        String lovhjemler = KlageMapper.formaterLovhjemlerForAvvistKlage(input, klagetEtterKlagefrist).get();
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }

}

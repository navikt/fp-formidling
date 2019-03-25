package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.behandling.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;

public class KlageMapperTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KlageMapper klageMapper;

    @Before
    public void setup() {
    }

    @Test
    public void skal_identifisere_opphevet_klage() {
        klageMapper = new KlageMapper(new KodeverkRepositoryImpl(repositoryRule.getEntityManager()),null);
        assertThat(klageMapper.erOpphevet(lagKlage(true))).isTrue();
    }

    @Test
    public void skal_identifisere_ikke_opphevet_klage() {
        klageMapper = new KlageMapper(new KodeverkRepositoryImpl(repositoryRule.getEntityManager()),null);
        assertThat(klageMapper.erOpphevet(lagKlage(false))).isFalse();
    }

    private Klage lagKlage(boolean opphevet) {
        return Klage.fraDto(lagKlageDto(opphevet));
    }

    private KlagebehandlingDto lagKlageDto(boolean opphevet) {
        KlagebehandlingDto dto = new KlagebehandlingDto();
        dto.setKlageVurderingResultatNFP(lagKlagevurderingResultatDto(opphevet));
        return dto;
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


}
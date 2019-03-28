package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnType;

public class KlageAvvistDokumentBrevMapperTest {

    @Test
    public void skal_mappe_kodeverk_til_brevkode() {
        List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();
        avvistÅrsaker.add(KlageAvvistÅrsak.KLAGET_FOR_SENT);
        avvistÅrsaker.add(KlageAvvistÅrsak.IKKE_KONKRET);
        List<AvvistGrunnType> avvistGrunnListe = KlageAvvistDokumentBrevMapper
                .avvistGrunnListeFra(avvistÅrsaker.stream()
                        .map(KlageAvvistÅrsak::getKode).collect(Collectors.toList())).getAvvistGrunn();
        assertThat(avvistGrunnListe).hasSize(2);
        assertThat(avvistGrunnListe.get(0).getAvvistGrunnKode()).isEqualTo(AvvistGrunnKode.ETTER_6_UKER);
        assertThat(avvistGrunnListe.get(1).getAvvistGrunnKode()).isEqualTo(AvvistGrunnKode.KLAGEIKKEKONKRET);
    }

}

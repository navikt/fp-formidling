package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.YtelseTypeKode;

public class UendretutfallBrevMapperTest {

    private UendretutfallBrevMapper mapper = new UendretutfallBrevMapper();

    @Test
    public void skal_mappe_korrekt() {
        assertThat(mapper.mapFagType(lagHendelse()).getYtelseType()).isEqualTo(YtelseTypeKode.ES);
    }

    private DokumentHendelse lagHendelse() {
        return DokumentHendelse.builder()
                .medBehandlingId(123l)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .build();
    }
}
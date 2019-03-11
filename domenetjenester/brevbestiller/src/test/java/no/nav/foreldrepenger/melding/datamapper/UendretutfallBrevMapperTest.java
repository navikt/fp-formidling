package no.nav.foreldrepenger.melding.datamapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.YtelseTypeKode;

public class UendretutfallBrevMapperTest {

    private UendretutfallBrevMapper mapper = new UendretutfallBrevMapper();

    @Test
    public void skal_mappe_korrekt() {
        assertThat(mapper.mapFagType(lagDto()).getYtelseType()).isEqualTo(YtelseTypeKode.FP);
    }

    private DokumentHendelseDto lagDto() {
        DokumentHendelseDto dto = new DokumentHendelseDto();
        dto.setYtelseType(FagsakYtelseType.FORELDREPENGER.getKode());
        return dto;
    }
}
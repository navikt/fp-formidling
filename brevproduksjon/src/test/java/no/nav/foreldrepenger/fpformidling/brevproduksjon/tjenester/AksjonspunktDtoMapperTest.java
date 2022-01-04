package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.aksjonspunkt.AksjonspunktStatus;

public class AksjonspunktDtoMapperTest {

    @Test
    public void skal_mappe_riktig_ap_status() {
        assertThat(AksjonspunktDtoMapper.finnAksjonspunktStatus("OPPR")).isEqualTo(AksjonspunktStatus.OPPRETTET);
        assertThat(AksjonspunktDtoMapper.finnAksjonspunktStatus("UTFO")).isEqualTo(AksjonspunktStatus.UTFØRT);
        assertThat(AksjonspunktDtoMapper.finnAksjonspunktStatus("AVBR")).isEqualTo(AksjonspunktStatus.AVBRUTT);
        assertThat(AksjonspunktDtoMapper.finnAksjonspunktStatus("-")).isEqualTo(AksjonspunktStatus.UDEFINERT);
        assertThat(AksjonspunktDtoMapper.finnAksjonspunktStatus("TULL")).isEqualTo(AksjonspunktStatus.UDEFINERT);
    }
}

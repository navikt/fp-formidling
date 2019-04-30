package no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt;

import java.util.List;

public class AksjonspunkterDto {
    private List<AksjonspunktDto> aksjonspunktDtos;

    public List<AksjonspunktDto> getAksjonspunktDtos() {
        return aksjonspunktDtos;
    }

    public void setAksjonspunktDtos(List<AksjonspunktDto> aksjonspunktDtos) {
        this.aksjonspunktDtos = aksjonspunktDtos;
    }
}

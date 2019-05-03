package no.nav.foreldrepenger.melding.datamapper.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;

@ApplicationScoped
public class AksjonspunktDtoMapper {

    public List<Aksjonspunkt> mapAksjonspunktFraDto(List<AksjonspunktDto> dtoer) {
        List<Aksjonspunkt> aksjonspunktList = new ArrayList<>();
        for (AksjonspunktDto aksjonspunktDto : dtoer) {
            aksjonspunktList.add(Aksjonspunkt.ny()
                    .medAksjonspunktDefinisjon(finnAksjonspunktDefinisjon(aksjonspunktDto.getDefinisjon().getKode()))
                    .build());
        }

        return aksjonspunktList;
    }

    private AksjonspunktDefinisjon finnAksjonspunktDefinisjon(String kode) {
        if (AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL;
        } else if (AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL;
        } else {
            return AksjonspunktDefinisjon.UDEFINERT;
        }
    }
}

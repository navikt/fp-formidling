package no.nav.foreldrepenger.melding.datamapper.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktStatus;

@ApplicationScoped
public class AksjonspunktDtoMapper {

    static AksjonspunktStatus finnAksjonspunktStatus(String kode) {
        if (AksjonspunktStatus.UTFØRT.getKode().equals(kode)) {
            return AksjonspunktStatus.UTFØRT;
        } else if (AksjonspunktStatus.OPPRETTET.getKode().equals(kode)) {
            return AksjonspunktStatus.OPPRETTET;
        } else if (AksjonspunktStatus.AVBRUTT.getKode().equals(kode)) {
            return AksjonspunktStatus.AVBRUTT;
        } else {
            return AksjonspunktStatus.UDEFINERT;
        }
    }

    private static AksjonspunktDefinisjon finnAksjonspunktDefinisjon(String kode) {
        if (AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL;
        } else if (AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL;
        } else if (AksjonspunktDefinisjon.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS;
        } else {
            return AksjonspunktDefinisjon.UDEFINERT;
        }
    }

    public List<Aksjonspunkt> mapAksjonspunktFraDto(List<AksjonspunktDto> dtoer) {
        List<Aksjonspunkt> aksjonspunktList = new ArrayList<>();
        for (AksjonspunktDto aksjonspunktDto : dtoer) {
            aksjonspunktList.add(Aksjonspunkt.ny()
                    .medAksjonspunktDefinisjon(finnAksjonspunktDefinisjon(aksjonspunktDto.getDefinisjon().getKode()))
                    .medAksjonspunktStatus(finnAksjonspunktStatus(aksjonspunktDto.getStatus().getKode()))
                    .build());
        }

        return aksjonspunktList;
    }
}

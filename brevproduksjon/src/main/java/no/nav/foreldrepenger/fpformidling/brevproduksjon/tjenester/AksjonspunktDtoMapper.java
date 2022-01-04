package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.fpformidling.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.fpformidling.aksjonspunkt.AksjonspunktStatus;
import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;

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
        } else if (AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT.getKode().equals(kode)) {
            return AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT;
        } else {
            return AksjonspunktDefinisjon.UDEFINERT;
        }
    }

    public static List<Aksjonspunkt> mapAksjonspunktFraDto(List<AksjonspunktDto> dtoer) {
        List<Aksjonspunkt> aksjonspunktList = new ArrayList<>();
        for (AksjonspunktDto aksjonspunktDto : dtoer) {
            aksjonspunktList.add(Aksjonspunkt.ny()
                    .medAksjonspunktDefinisjon(finnAksjonspunktDefinisjon(aksjonspunktDto.definisjon().getKode()))
                    .medAksjonspunktStatus(finnAksjonspunktStatus(aksjonspunktDto.status().getKode()))
                    .build());
        }

        return aksjonspunktList;
    }
}

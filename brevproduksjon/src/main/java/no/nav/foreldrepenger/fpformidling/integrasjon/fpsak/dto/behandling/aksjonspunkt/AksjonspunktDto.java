package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AksjonspunktDto(String definisjon, String status) {
}

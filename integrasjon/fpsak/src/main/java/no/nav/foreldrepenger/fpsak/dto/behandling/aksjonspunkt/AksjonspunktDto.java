package no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AksjonspunktDto(KodeDto definisjon, KodeDto status) {
}

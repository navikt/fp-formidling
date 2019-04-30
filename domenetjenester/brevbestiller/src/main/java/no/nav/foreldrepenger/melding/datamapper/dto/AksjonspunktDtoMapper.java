package no.nav.foreldrepenger.melding.datamapper.dto;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunkterDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class AksjonspunktDtoMapper {
    private KodeverkRepository kodeverkRepository;

    public AksjonspunktDtoMapper() {
    }

    @Inject
    public AksjonspunktDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public List<Aksjonspunkt> mapAksjonspunktFraDto(AksjonspunkterDto dto) {
        return null;
    }
}

package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class BehandlingMapper {

    private KodeverkRepository kodeverkRepository;

    public BehandlingMapper() {
    }

    @Inject
    public BehandlingMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public int finnAntallUkerBehandlingsfrist(Behandling behandling) {
        BehandlingType behandlingType = kodeverkRepository.finn(BehandlingType.class, behandling.getType());
        return behandlingType.getBehandlingstidFristUker();
    }
}

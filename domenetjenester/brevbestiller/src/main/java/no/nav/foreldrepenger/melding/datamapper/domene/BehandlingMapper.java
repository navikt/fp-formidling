package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.DokumentMapperKonstanter.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.DokumentMapperKonstanter.MEDHOLD;
import static no.nav.foreldrepenger.melding.datamapper.DokumentMapperKonstanter.REVURDERING;

import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
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
        BehandlingType behandlingType = kodeverkRepository.finn(BehandlingType.class, behandling.getBehandlingType());
        return behandlingType.getBehandlingstidFristUker();
    }

    public String utledBehandlingsTypeForPositivtVedtak(Behandling behandling) {
        String behandlingsType;
        Stream<BehandlingÅrsakType> årsaker = behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingArsakType).map(kode -> kodeverkRepository.finn(BehandlingÅrsakType.class, kode));
        boolean etterKlage = årsaker.anyMatch(BehandlingÅrsakType.årsakerEtterKlageBehandling()::contains);
        if (etterKlage) {
            behandlingsType = MEDHOLD;
        } else {
            behandlingsType = (BehandlingType.REVURDERING.getKode().equals(behandling.getBehandlingType())) ? REVURDERING : FØRSTEGANGSSØKNAD;
        }
        return behandlingsType;
    }
}

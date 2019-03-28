package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.MEDHOLD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;

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

    public BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }

    public int finnAntallUkerBehandlingsfrist(String behandlingType) {
        return finnBehandlingType(behandlingType).getBehandlingstidFristUker();
    }

    public String finnBehandlingTypeForDokument(Behandling behandling) {
        return gjelderEndringsøknad(behandling) ?
                ENDRINGSSØKNAD :
                behandling.getBehandlingType();
    }

    private boolean gjelderEndringsøknad(Behandling behandling) {
        return behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType)
                .anyMatch(type -> BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER.getKode().equals(type));
    }

    public String utledBehandlingsTypeForPositivtVedtak(Behandling behandling) {
        String behandlingsType;
        Stream<BehandlingÅrsakType> årsaker = behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType).map(kode -> kodeverkRepository.finn(BehandlingÅrsakType.class, kode));
        boolean etterKlage = årsaker.anyMatch(BehandlingÅrsakType.årsakerEtterKlageBehandling()::contains);
        if (etterKlage) {
            behandlingsType = MEDHOLD;
        } else {
            behandlingsType = (BehandlingType.REVURDERING.getKode().equals(behandling.getBehandlingType())) ? REVURDERING : FØRSTEGANGSSØKNAD;
        }
        return behandlingsType;
    }
}

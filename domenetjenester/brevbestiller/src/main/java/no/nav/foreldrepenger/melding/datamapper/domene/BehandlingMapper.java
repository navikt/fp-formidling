package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class BehandlingMapper {

    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;

    public BehandlingMapper() {
    }

    @Inject
    public BehandlingMapper(KodeverkRepository kodeverkRepository,
                            BehandlingRestKlient behandlingRestKlient) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public Behandling hentBehandling(long behandlingId) {
        return new Behandling(behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingId)));
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

    static boolean gjelderEndringsøknad(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER.getKode());
    }

    public static boolean erRevurderingPgaFødselshendelse(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_HENDELSE_FØDSEL.getKode());
    }

    static List<String> getBehandlingÅrsakStringListe(Behandling behandling) {
        return behandling.getBehandlingÅrsaker().stream().map(BehandlingÅrsak::getBehandlingÅrsakType).collect(Collectors.toList());
    }

    public BehandlingsTypeKode utledBehandlingsTypeInnvilgetFP(Behandling behandling) {
        return BehandlingType.REVURDERING.getKode().equals(behandling.getBehandlingType()) ?
                BehandlingsTypeKode.REVURDERING : BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    public BehandlingsTypeType utledBehandlingsTypeInnvilgetES(Behandling behandling) {
        Stream<BehandlingÅrsakType> årsaker = behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType).map(kode -> kodeverkRepository.finn(BehandlingÅrsakType.class, kode));
        boolean etterKlage = årsaker.anyMatch(BehandlingÅrsakType.årsakerEtterKlageBehandling()::contains);
        if (etterKlage) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingType.REVURDERING.getKode().equals(behandling.getBehandlingType()) ? BehandlingsTypeType.REVURDERING : BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }
}

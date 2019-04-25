package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BehandlingstypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsTypeKode;
import no.nav.vedtak.util.StringUtils;

public class BehandlingMapper {

    public BehandlingMapper() {
        //CDI
    }

    public static Optional<String> avklarFritekst(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (!StringUtils.nullOrEmpty(dokumentHendelse.getFritekst())) {
            return Optional.of(dokumentHendelse.getFritekst());
        } else if (behandling.getBehandlingsresultat() != null &&
                !StringUtils.nullOrEmpty(behandling.getBehandlingsresultat().getAvslagarsakFritekst())) {
            return Optional.of(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        }
        return Optional.empty();
    }

    public static int finnAntallUkerBehandlingsfrist(BehandlingType behandlingType) {
        return behandlingType.getBehandlingstidFristUker();
    }

    public static String finnBehandlingTypeForDokument(Behandling behandling) {
        return gjelderEndringsøknad(behandling) ?
                ENDRINGSSØKNAD :
                behandling.getBehandlingType().getKode();
    }

    static boolean gjelderEndringsøknad(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    public static boolean erRevurderingPgaFødselshendelse(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_HENDELSE_FØDSEL);
    }

    static List<BehandlingÅrsakType> getBehandlingÅrsakStringListe(Behandling behandling) {
        return behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType)
                .collect(Collectors.toList());
    }

    public static BehandlingsTypeKode utledBehandlingsTypeInnvilgetFP(Behandling behandling) {
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ?
                BehandlingsTypeKode.REVURDERING : BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    public static BehandlingsTypeType utledBehandlingsTypeInnvilgetES(Behandling behandling) {
        Stream<BehandlingÅrsakType> årsaker = behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType);
        boolean etterKlage = årsaker.anyMatch(BehandlingÅrsakType.årsakerEtterKlageBehandling()::contains);
        if (etterKlage) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ? BehandlingsTypeType.REVURDERING : BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }

    public static BehandlingstypeType utledBehandlingsTypeAvslagES(Behandling behandling) {
        boolean erRevurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        return erRevurdering ? BehandlingstypeType.REVURDERING : BehandlingstypeType.SØKNAD;
    }
}

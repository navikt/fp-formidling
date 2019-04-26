package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BehandlingstypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsResultatKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;
import no.nav.vedtak.util.StringUtils;

public class BehandlingMapper {

    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";
    private static Map<String, KonsekvensForYtelseKode> konsekvensForYtelseKodeMap = new HashMap<>();

    static {
        konsekvensForYtelseKodeMap.put(ENDRING_BEREGNING_OG_UTTAK, KonsekvensForYtelseKode.ENDRING_I_BEREGNING_OG_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), KonsekvensForYtelseKode.ENDRING_I_BEREGNING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.INGEN_ENDRING.getKode(), KonsekvensForYtelseKode.INGEN_ENDRING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode(), KonsekvensForYtelseKode.ENDRING_I_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.FORELDREPENGER_OPPHØRER.getKode(), KonsekvensForYtelseKode.FORELDREPENGER_OPPHØRER);
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

    public static String utledBehandlingsTypeForAvslagVedtak(Behandling behandling) {
        if (behandling.erRevurdering()) {
            return REVURDERING;
        } else if (behandling.erFørstegangssøknad() && behandling.gjelderForeldrepenger()) {
            return FØRSTEGANGSSØKNAD;
        }
        return SØKNAD;
    }

    public static BehandlingstypeType utledBehandlingsTypeAvslagES(Behandling behandling) {
        boolean erRevurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        return erRevurdering ? BehandlingstypeType.REVURDERING : BehandlingstypeType.SØKNAD;
    }

    public static boolean erEndringMedEndretInntektsmelding(Behandling behandling) {
        return erEndring(behandling.getBehandlingType())
                && getBehandlingÅrsakTypeListe(behandling).contains((BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING));
    }

    private static List<BehandlingÅrsakType> getBehandlingÅrsakTypeListe(Behandling behandling) {
        return behandling.getBehandlingÅrsaker().stream().map(BehandlingÅrsak::getBehandlingÅrsakType).collect(Collectors.toList());
    }

    private static boolean erEndring(BehandlingType behandlingType) {
        return BehandlingType.REVURDERING.equals(behandlingType)
                || BehandlingType.KLAGE.equals(behandlingType);
    }

    public static BehandlingsResultatKode tilBehandlingsResultatKode(BehandlingResultatType behandlingsresultatkode) {
        if (BehandlingResultatType.INNVILGET.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.INNVILGET;
        } else if (BehandlingResultatType.AVSLÅTT.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.AVSLÅTT;
        } else if (BehandlingResultatType.OPPHØR.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.OPPHØR;
        } else if (BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.FORELDREPENGER_ENDRET;
        } else {
            return BehandlingsResultatKode.INGEN_ENDRING;
        }
    }

    public static KonsekvensForYtelseKode finnKonsekvensForYtelseKode(Behandlingsresultat behandlingsresultat) {
        String konsekvens = kodeFra(behandlingsresultat.getKonsekvenserForYtelsen());
        if (konsekvensForYtelseKodeMap.containsKey(konsekvens)) {
            return konsekvensForYtelseKodeMap.get(konsekvens);
        }
        return KonsekvensForYtelseKode.INGEN_ENDRING;
    }

    private static String kodeFra(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) ?
                    ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        } else {
            return konsekvenserForYtelsen.isEmpty() ?
                    KonsekvensForYtelsen.UDEFINERT.getKode() : konsekvenserForYtelsen.get(0).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
        }
    }
}

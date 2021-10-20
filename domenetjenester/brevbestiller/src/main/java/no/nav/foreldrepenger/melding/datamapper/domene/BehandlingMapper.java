package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;

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
        if (dokumentHendelse.getFritekst() != null && !dokumentHendelse.getFritekst().isEmpty()) {
            return Optional.of(dokumentHendelse.getFritekst());
        } else if (behandling.getBehandlingsresultat() != null) {
            return Optional.ofNullable(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        }
        return Optional.empty();
    }

    public static boolean gjelderEndringsøknad(Behandling behandling) {
        // endringssøknad kan være satt ved førstegangsbehandling og henleggelse pga håndtering av tapte dager
        return behandling.erRevurdering() && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    public static boolean erTermindatoEndret(FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse) {
        if (originalFamiliehendelse.isEmpty()) {
            return false;
        }
        return !originalFamiliehendelse.get().getTermindato().equals(familieHendelse.getTermindato());
    }

    public static Boolean erEndretFraAvslått(Optional<Behandling> orginalBehandling) {
        return orginalBehandling.map(forrigeBehandling -> forrigeBehandling.getBehandlingsresultat().erAvslått())
                .orElse(false);
    }

    public static boolean erRevurderingPgaEndretBeregningsgrunnlag(Behandling revurdering) {
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = revurdering.getBehandlingsresultat().getKonsekvenserForYtelsen();
        boolean kunEndringIBeregning = konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING) &&
                konsekvenserForYtelsen.size() == 1;

        return kunEndringIBeregning;
    }

    public static BehandlingsTypeType utledBehandlingsTypeInnvilgetES(Behandling behandling) {
        boolean etterKlage = BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
        if (etterKlage) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ? BehandlingsTypeType.REVURDERING : BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }

    public static boolean erMedhold(Behandling behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
    }

    public static String utledBehandlingsTypeForAvslagVedtak(Behandling behandling, DokumentHendelse dokumentHendelse) {
        if (behandling.erRevurdering()) {
            return REVURDERING;
        } else if (behandling.erFørstegangssøknad() && dokumentHendelse.getYtelseType().gjelderForeldrepenger()) {
            return FØRSTEGANGSSØKNAD;
        }
        return SØKNAD;
    }

    public static String kodeFra(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) ?
                    ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        } else {
            return konsekvenserForYtelsen.isEmpty() ?
                    KonsekvensForYtelsen.UDEFINERT.getKode() : konsekvenserForYtelsen.get(0).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
        }
    }
}

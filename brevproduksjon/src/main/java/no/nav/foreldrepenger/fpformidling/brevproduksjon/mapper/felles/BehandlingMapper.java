package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public class BehandlingMapper {

    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

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

    public static boolean erMedhold(Behandling behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
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

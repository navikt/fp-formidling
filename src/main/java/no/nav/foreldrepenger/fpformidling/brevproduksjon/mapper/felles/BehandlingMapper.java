package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.BehandlingÅrsakType;

public final class BehandlingMapper {

    private BehandlingMapper() {
    }

    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

    public static boolean erTermindatoEndret(FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse) {
        if (originalFamiliehendelse.isEmpty()) {
            return false;
        }
        return !originalFamiliehendelse.get().termindato().equals(familieHendelse.termindato());
    }

    public static Boolean erEndretFraAvslått(Optional<Behandling> orginalBehandling) {
        return orginalBehandling.map(forrigeBehandling -> forrigeBehandling.getBehandlingsresultat().erAvslått()).orElse(false);
    }

    public static boolean erRevurderingPgaEndretBeregningsgrunnlag(Behandling revurdering) {
        var konsekvenserForYtelsen = revurdering.getBehandlingsresultat().getKonsekvenserForYtelsen();
        return konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING) && konsekvenserForYtelsen.size() == 1;
    }

    public static boolean erMedhold(Behandling behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
    }

    public static String kodeFra(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return konsekvenserForYtelsen.contains(
                KonsekvensForYtelsen.ENDRING_I_UTTAK) ? ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        }
        return konsekvenserForYtelsen.isEmpty() ? KonsekvensForYtelsen.UDEFINERT.getKode() : konsekvenserForYtelsen.get(0)
            .getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
    }
}

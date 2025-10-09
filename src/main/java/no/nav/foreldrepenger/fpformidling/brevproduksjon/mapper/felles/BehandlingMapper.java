package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public final class BehandlingMapper {

    private BehandlingMapper() {
    }

    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

    public static boolean erTermindatoEndret(BrevGrunnlag.FamilieHendelse familieHendelse, BrevGrunnlag.FamilieHendelse originalFamiliehendelse) {
        return !originalFamiliehendelse.termindato().equals(familieHendelse.termindato());
    }

    public static boolean erRevurderingPgaEndretBeregningsgrunnlag(BrevGrunnlag revurdering) {
        var konsekvenserForYtelsen = revurdering.behandlingsresultat().konsekvenserForYtelsen();
        return konsekvenserForYtelsen.contains(BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING) && konsekvenserForYtelsen.size() == 1;
    }

    public static boolean erMedhold(BrevGrunnlag behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().map(KodeverkMapper::mapBehandlingÅrsak).anyMatch(behandling::harBehandlingÅrsak);
    }

    public static String kodeFra(List<BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen) {
        var mapped = konsekvenserForYtelsen.stream().map(KodeverkMapper::mapKonsekvensForYtelsen).toList();
        if (mapped.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return mapped.contains(
                KonsekvensForYtelsen.ENDRING_I_UTTAK) ? ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        }
        return mapped.isEmpty() ? KonsekvensForYtelsen.UDEFINERT.getKode() : mapped.getFirst()
            .getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
    }
}

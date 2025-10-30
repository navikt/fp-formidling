package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public final class BehandlingMapper {

    private BehandlingMapper() {
    }

    public static boolean erTermindatoEndret(BrevGrunnlagDto.FamilieHendelse familieHendelse, BrevGrunnlagDto.FamilieHendelse originalFamiliehendelse) {
        return !originalFamiliehendelse.termindato().equals(familieHendelse.termindato());
    }

    public static boolean erRevurderingPgaEndretBeregningsgrunnlag(BrevGrunnlagDto revurdering) {
        var konsekvenserForYtelsen = revurdering.behandlingsresultat().konsekvenserForYtelsen();
        return konsekvenserForYtelsen.contains(BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING) && konsekvenserForYtelsen.size() == 1;
    }

    public static boolean erMedhold(BrevGrunnlagDto behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().map(KodeverkMapper::mapBehandlingÅrsak).anyMatch(behandling::harBehandlingÅrsak);
    }

    public static KonsekvensForYtelsen kodeFra(List<BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen) {
        var mapped = konsekvenserForYtelsen.stream().map(KodeverkMapper::mapKonsekvensForYtelsen).toList();
        if (mapped.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return mapped.contains(
                KonsekvensForYtelsen.ENDRING_I_UTTAK) ? KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING;
        }
        return mapped.isEmpty() ? null : mapped.getFirst(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
    }
}

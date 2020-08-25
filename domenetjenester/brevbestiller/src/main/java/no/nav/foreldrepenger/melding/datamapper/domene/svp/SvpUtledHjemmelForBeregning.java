package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemlerForBeregning;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;

final class SvpUtledHjemmelForBeregning {

    static String utled(Beregningsgrunnlag beregningsgrunnlag, Behandling behandling) {

        boolean revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        boolean innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        String konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());

        // TODO (ALM) : Fix me! Workaround for å unngå manuelt arbeid for saksbehandlere - Se TFP-1032
        // Fpsak sender nå hjemmel 14-7, men SVP skal referer til hjemmel 14-4. Fpsak burde egentlig sende 14-4 for SVP,
        // men hjemmelen blir utledet kun for FP (dvs. 14-7) i dag. Denne hacken erstatter 14-7 med 14-4 for SVP før
        // brevet blir generert. Dette er en workaround inntil hjemmel for flere ytelsestyper (SVP, ES, FP, etc.) blir
        // implementert i fpsak.
        String hjemmel = beregningsgrunnlag.getHjemmel().getNavn();
        if (hjemmel != null) {
            hjemmel = hjemmel.replace("14-7", "14-4");
        }

        return formaterLovhjemlerForBeregning(hjemmel, konsekvensForYtelsen, innvilget && revurdering);

    }

}

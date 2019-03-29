package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;

@ApplicationScoped
public class IAYMapper {

    private BehandlingRestKlient behandlingRestKlient;

    public InntektArbeidYtelse hentInntektArbeidYtelse(Behandling behandling) {
        return new InntektArbeidYtelse(behandlingRestKlient.hentInntektArbeidYtelseDto(behandling.getResourceLinkDtos()));
    }

    public static Inntektsmelding hentVillkårligInntektsmelding(InntektArbeidYtelse iay) {
        return iay.getInntektsmeldinger().stream().findAny().orElseThrow(() -> {
            throw new IllegalStateException("Finner ingen inntektsmelding");
        });
    }

    ;

}

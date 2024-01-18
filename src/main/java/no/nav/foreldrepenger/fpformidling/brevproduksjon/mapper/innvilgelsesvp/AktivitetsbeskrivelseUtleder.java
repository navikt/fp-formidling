package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;

public final class AktivitetsbeskrivelseUtleder {

    private AktivitetsbeskrivelseUtleder() {
    }

    public static String utledAktivitetsbeskrivelse(TilkjentYtelseAndel andel, AktivitetStatus aktivitetStatus) {
        var arbeidsgiverOpt = andel.getArbeidsgiver();
        if (arbeidsgiverOpt.isPresent()) {
            return arbeidsgiverOpt.get().navn();
        }
        if (aktivitetStatus.erFrilanser()) {
            return "Som frilanser";
        }
        if (aktivitetStatus.erSelvstendigNæringsdrivende()) {
            return "Som næringsdrivende";
        }
        return "";
    }
}

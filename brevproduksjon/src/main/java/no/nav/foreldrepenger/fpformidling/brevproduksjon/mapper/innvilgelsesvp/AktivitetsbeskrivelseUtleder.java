package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public final class AktivitetsbeskrivelseUtleder {

    public static String utledAktivitetsbeskrivelse(TilkjentYtelseAndel andel, AktivitetStatus aktivitetStatus) {
        Optional<Arbeidsgiver> arbeidsgiverOpt = andel.getArbeidsgiver();
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

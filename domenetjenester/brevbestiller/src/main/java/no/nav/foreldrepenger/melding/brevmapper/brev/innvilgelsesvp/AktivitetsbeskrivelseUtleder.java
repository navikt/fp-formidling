package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import java.util.Optional;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class AktivitetsbeskrivelseUtleder {

    public static String utledAktivitetsbeskrivelse(BeregningsresultatAndel andel, AktivitetStatus aktivitetStatus) {
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

package no.nav.foreldrepenger.fpformidling.tilkjentytelse;

import java.math.BigDecimal;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class TilkjentYtelseAndel {
    private final int dagsats;
    private final AktivitetStatus aktivitetStatus;
    private final boolean erArbeidsgiverMottaker;
    private final BigDecimal stillingsprosent;
    private final Arbeidsgiver arbeidsgiver;
    private final ArbeidsforholdRef arbeidsforholdRef;
    private final boolean erBrukerMottaker;
    private final int utbetalesTilBruker;

    private TilkjentYtelseAndel(Builder builder) {
        dagsats = builder.dagsats;
        stillingsprosent = builder.stillingsprosent;
        aktivitetStatus = builder.aktivitetStatus;
        arbeidsgiver = builder.arbeidsgiver;
        arbeidsforholdRef = builder.arbeidsforholdRef;
        erBrukerMottaker = builder.erBrukerMottaker;
        erArbeidsgiverMottaker = builder.erArbeidsgiverMottaker;
        utbetalesTilBruker = builder.utbetalesTilBruker;
    }

    public static Builder ny() {
        return new Builder();
    }

    public int getDagsats() {
        return dagsats;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }

    public int getUtbetalesTilBruker() {
        return utbetalesTilBruker;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public ArbeidsforholdRef getArbeidsforholdRef() {
        return arbeidsforholdRef;
    }

    public boolean erBrukerMottaker() {
        return erBrukerMottaker;
    }

    public boolean erArbeidsgiverMottaker() {
        return erArbeidsgiverMottaker;
    }

    public static final class Builder {
        private int dagsats;
        private AktivitetStatus aktivitetStatus;
        private boolean erArbeidsgiverMottaker;
        private Arbeidsgiver arbeidsgiver;
        private ArbeidsforholdRef arbeidsforholdRef;
        private BigDecimal stillingsprosent;
        private boolean erBrukerMottaker;
        private int utbetalesTilBruker;

        private Builder() {
        }

        public Builder medDagsats(int dagsats) {
            this.dagsats = dagsats;
            return this;
        }

        public Builder medStillingsprosent(BigDecimal stillingsprosent) {
            this.stillingsprosent = stillingsprosent;
            return this;
        }

        public Builder medUtbetalesTilBruker(int utbetalesTilBruker) {
            this.utbetalesTilBruker = utbetalesTilBruker;
            return this;
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medArbeidsgiver(Arbeidsgiver arbeidsgiver) {
            this.arbeidsgiver = arbeidsgiver;
            return this;
        }

        public Builder medArbeidsforholdRef(ArbeidsforholdRef arbeidsforholdRef) {
            this.arbeidsforholdRef = arbeidsforholdRef;
            return this;
        }

        public Builder medErBrukerMottaker(boolean erBrukerMottaker) {
            this.erBrukerMottaker = erBrukerMottaker;
            return this;
        }


        public Builder medErArbeidsgiverMottaker(boolean erArbeidsgiverMottaker) {
            this.erArbeidsgiverMottaker = erArbeidsgiverMottaker;
            return this;
        }

        public TilkjentYtelseAndel build() {
            return new TilkjentYtelseAndel(this);
        }
    }
}

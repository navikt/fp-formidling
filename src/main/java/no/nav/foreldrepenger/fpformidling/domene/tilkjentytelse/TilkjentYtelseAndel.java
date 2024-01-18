package no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

public class TilkjentYtelseAndel {
    private final int dagsats;
    private final AktivitetStatus aktivitetStatus;
    private final boolean erArbeidsgiverMottaker;
    private final BigDecimal stillingsprosent;
    private final Arbeidsgiver arbeidsgiver;
    private final ArbeidsforholdRef arbeidsforholdRef;
    private final boolean erBrukerMottaker;
    private final int utbetalesTilBruker;
    private final BigDecimal utbetalingsgrad;

    private TilkjentYtelseAndel(Builder builder) {
        dagsats = builder.dagsats;
        stillingsprosent = builder.stillingsprosent;
        aktivitetStatus = builder.aktivitetStatus;
        arbeidsgiver = builder.arbeidsgiver;
        arbeidsforholdRef = builder.arbeidsforholdRef;
        erBrukerMottaker = builder.erBrukerMottaker;
        erArbeidsgiverMottaker = builder.erArbeidsgiverMottaker;
        utbetalesTilBruker = builder.utbetalesTilBruker;
        utbetalingsgrad = builder.utbetalingsgrad;
    }

    @Override
    public String toString() {
        return "TilkjentYtelseAndel{" + "dagsats=" + dagsats + ", aktivitetStatus=" + aktivitetStatus + ", erArbeidsgiverMottaker="
            + erArbeidsgiverMottaker + ", stillingsprosent=" + stillingsprosent + ", arbeidsgiver=" + arbeidsgiver + ", arbeidsforholdRef="
            + arbeidsforholdRef + ", erBrukerMottaker=" + erBrukerMottaker + ", utbetalesTilBruker=" + utbetalesTilBruker + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TilkjentYtelseAndel that = (TilkjentYtelseAndel) o;
        return dagsats == that.dagsats && erArbeidsgiverMottaker == that.erArbeidsgiverMottaker && erBrukerMottaker == that.erBrukerMottaker
            && utbetalesTilBruker == that.utbetalesTilBruker && aktivitetStatus == that.aktivitetStatus && Objects.equals(stillingsprosent,
            that.stillingsprosent) && Objects.equals(arbeidsgiver, that.arbeidsgiver) && Objects.equals(arbeidsforholdRef, that.arbeidsforholdRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dagsats, aktivitetStatus, erArbeidsgiverMottaker, stillingsprosent, arbeidsgiver, arbeidsforholdRef, erBrukerMottaker,
            utbetalesTilBruker);
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

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
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
        private BigDecimal utbetalingsgrad;

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
        public Builder medUtbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public TilkjentYtelseAndel build() {
            return new TilkjentYtelseAndel(this);
        }
    }
}

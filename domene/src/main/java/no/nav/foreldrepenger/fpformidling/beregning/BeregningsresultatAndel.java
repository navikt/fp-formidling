package no.nav.foreldrepenger.fpformidling.beregning;

import java.math.BigDecimal;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class BeregningsresultatAndel {
    private int dagsats;
    private BigDecimal stillingsprosent;
    private AktivitetStatus aktivitetStatus;
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;
    private boolean brukerErMottaker;
    private boolean arbeidsgiverErMottaker;
    private int tilSoker;

    private BeregningsresultatAndel(Builder builder) {
        dagsats = builder.dagsats;
        stillingsprosent = builder.stillingsprosent;
        aktivitetStatus = builder.aktivitetStatus;
        arbeidsgiver = builder.arbeidsgiver;
        arbeidsforholdRef = builder.arbeidsforholdRef;
        brukerErMottaker = builder.brukerErMottaker;
        arbeidsgiverErMottaker = builder.arbeidsgiverErMottaker;
        tilSoker = builder.tilSoker;
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

    public int getTilSoker() { return tilSoker; }

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
        return brukerErMottaker;
    }

    public boolean erArbeidsgiverMottaker() {
        return arbeidsgiverErMottaker;
    }

    public static final class Builder {
        private int dagsats;
        private BigDecimal stillingsprosent;
        private AktivitetStatus aktivitetStatus;
        private Arbeidsgiver arbeidsgiver;
        private ArbeidsforholdRef arbeidsforholdRef;
        private boolean brukerErMottaker;
        private boolean arbeidsgiverErMottaker;
        private int tilSoker;

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

        public Builder medTilSoker(int tilSoker) {
            this.tilSoker = tilSoker;
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

        public Builder medBrukerErMottaker(boolean brukerErMottaker) {
            this.brukerErMottaker = brukerErMottaker;
            return this;
        }


        public Builder medArbeidsgiverErMottaker(boolean arbeidsgiverErMottaker) {
            this.arbeidsgiverErMottaker = arbeidsgiverErMottaker;
            return this;
        }

        public BeregningsresultatAndel build() {
            return new BeregningsresultatAndel(this);
        }
    }
}

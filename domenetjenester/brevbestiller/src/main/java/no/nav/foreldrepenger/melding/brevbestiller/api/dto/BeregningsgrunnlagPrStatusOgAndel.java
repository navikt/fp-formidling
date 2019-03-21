package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

public class BeregningsgrunnlagPrStatusOgAndel {
    private int dagsatsArbeidsgiver;
    private int dagsatsBruker;
    private String aktivitetStatus; // Kodeliste.AktivitetStatus
    private String orginalDagsatsFraTilstøtendeYtelse;
    private int bruttoPrÅr;
    private int besteberegningPrÅr;
    private int overstyrtPrÅr;
    private int pgiSnitt;
    private int pgi1;
    private int pgi2;
    private int pgi3;
    private String opptjeningAktivitetType; // Kodeliste.OpptjeningAktivitetType

    private Object beregningsperiode;

}

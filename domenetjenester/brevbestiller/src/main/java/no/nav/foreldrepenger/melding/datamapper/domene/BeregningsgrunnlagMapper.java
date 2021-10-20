package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.StatusTypeKode;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagMapper {
    public BeregningsgrunnlagMapper() {
        // CDI Proxy
    }

    private static Map<AktivitetStatus, StatusTypeKode> aktivitetStatusKodeStatusTypeKodeMap = new HashMap<>();
    private static Map<AktivitetStatus, List<AktivitetStatus>> kombinerteRegelStatuserMap = new HashMap<>();

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN,
                List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    static {
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSTAKER, StatusTypeKode.ARBEIDSTAKER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.FRILANSER, StatusTypeKode.FRILANSER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, StatusTypeKode.SELVSTENDIG_NÆRINGSDRIVENDE);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL, StatusTypeKode.KOMBINERT_AT_FL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN, StatusTypeKode.KOMBINERT_AT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_SN, StatusTypeKode.KOMBINERT_AT_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_FL_SN, StatusTypeKode.KOMBINERT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.DAGPENGER, StatusTypeKode.DAGPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSAVKLARINGSPENGER, StatusTypeKode.ARBEIDSAVKLARINGSPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.MILITÆR_ELLER_SIVIL, StatusTypeKode.MILITÆR_ELLER_SIVIL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.BRUKERS_ANDEL, StatusTypeKode.BRUKERSANDEL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KUN_YTELSE, StatusTypeKode.KUN_YTELSE);
    }

    public static boolean militærEllerSivilTjeneste(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getAktivitetStatuser().contains(AktivitetStatus.MILITÆR_ELLER_SIVIL);
    }

    public static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus,
            List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagPrStatusOgAndel> resultatListe;
        if (AktivitetStatus.KUN_YTELSE.equals(bgAktivitetStatus.aktivitetStatus())) {
            return bgpsaListe;
        }
        if (bgAktivitetStatus.aktivitetStatus().harKombinertStatus()) {
            List<AktivitetStatus> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.aktivitetStatus());
            resultatListe = bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        } else {
            resultatListe = bgpsaListe.stream().filter(andel -> bgAktivitetStatus.aktivitetStatus().equals(andel.getAktivitetStatus()))
                    .collect(Collectors.toList());
        }
        if (resultatListe.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(AktivitetStatus::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.aktivitetStatus(), sb));
        }
        return resultatListe;
    }

    public static BigDecimal getMånedsinntekt(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    public static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::navn);
    }

    public static BigDecimal getBgBruttoPrÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
    }

    private static BeregningsgrunnlagPeriode finnFørstePeriode(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);
    }

    public static BigDecimal finnSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getGrunnbeløp().multipliser(6).getVerdi();
    }

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    // kun SVP: avkortetPerÅr gir faktisk beregningsgrunnlag for alle
    // arbeidsforhold, og ikke bare arbeidsforholdet det søkes om - en verdi kun på
    // dto
    public static BigDecimal getAvkortetPrAarSVP(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getAvkortetPrÅr();
    }

    public static long getHalvGOrElseZero(Optional<Beregningsgrunnlag> beregningsgrunnlag) {
        return beregningsgrunnlag.map(Beregningsgrunnlag::getGrunnbeløp).map(Beløp::getVerdi).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP).longValue();
    }
}

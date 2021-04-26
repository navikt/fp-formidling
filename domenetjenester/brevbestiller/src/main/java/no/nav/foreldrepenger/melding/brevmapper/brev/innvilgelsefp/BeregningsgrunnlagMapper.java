package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class BeregningsgrunnlagMapper {

    private static final Map<AktivitetStatus, List<AktivitetStatus>> kombinerteRegelStatuserMap = new HashMap<>();

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN, List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    public static long finnBrutto(Beregningsgrunnlag beregningsgrunnlag) {
        AtomicLong sum = new AtomicLong();
        finnBgpsaListe(beregningsgrunnlag)
                .forEach(andel -> {
                    if (andel.getAvkortetPrÅr() != null) {
                        sum.addAndGet(andel.getAvkortetPrÅr().longValue());
                    } else if (andel.getBruttoPrÅr() != null) {
                        sum.addAndGet(andel.getBruttoPrÅr().longValue());
                    }
                });
        return sum.get();
    }

    public static BigDecimal finnSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getGrunnbeløp().multipliser(6).getVerdi();
    }

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    public static List<BeregningsgrunnlagRegel> mapRegelListe(Beregningsgrunnlag beregningsgrunnlag) {
        List<BeregningsgrunnlagRegel> beregningsgrunnlagregler = new ArrayList<>();
        List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe = finnBgpsaListe(beregningsgrunnlag);

        for (BeregningsgrunnlagAktivitetStatus bgAktivitetStatus : beregningsgrunnlag.getAktivitetStatuser()) {
            BeregningsgrunnlagRegel.Builder builder = BeregningsgrunnlagRegel.ny();
            List<BeregningsgrunnlagPrStatusOgAndel> filtrertListe = finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe);
            builder.medAktivitetStatus(bgAktivitetStatus.getAktivitetStatus());
            builder.medAndelListe(mapAndelListe(filtrertListe));
            builder.medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(tellAntallArbeidsforholdIBeregningUtenSluttpakke(filtrertListe));
            builder.medSnNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
            beregningsgrunnlagregler.add(builder.build());
        }
        return beregningsgrunnlagregler;
    }

    public static boolean harBruktBruttoBeregningsgrunnlag(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return (beregningsgrunnlagregler.size() > 1 || (minstEnRegelHarKombinertAktivitetStatus(beregningsgrunnlagregler)));
    }

    private static boolean minstEnRegelHarKombinertAktivitetStatus(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return beregningsgrunnlagregler.stream().anyMatch(regel -> regel.getAktivitetStatus().harKombinertStatus());
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> finnBgpsaListe(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBeregningsgrunnlagPrStatusOgAndelList();
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus,
                                                                                          List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagPrStatusOgAndel> resultatListe;
        if (AktivitetStatus.KUN_YTELSE.equals(bgAktivitetStatus.getAktivitetStatus())) {
            return bgpsaListe;
        }
        if (bgAktivitetStatus.getAktivitetStatus().harKombinertStatus()) {
            List<AktivitetStatus> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.getAktivitetStatus());
            resultatListe = bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        } else {
            resultatListe = bgpsaListe.stream().filter(andel -> bgAktivitetStatus.getAktivitetStatus().equals(andel.getAktivitetStatus())).collect(Collectors.toList());
        }
        if (resultatListe.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(AktivitetStatus::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.getAktivitetStatus(), sb));
        }
        return resultatListe;
    }

    private static BeregningsgrunnlagAndel lagBeregningsgrunnlagAndel(BeregningsgrunnlagPrStatusOgAndel andel) {
        BeregningsgrunnlagAndel.Builder builder = BeregningsgrunnlagAndel.ny();

        builder.medAktivitetStatus(andel.getAktivitetStatus());
        builder.medDagsats(andel.getDagsats());
        builder.medEtterlønnSluttpakke(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType()));

        BigDecimal bgBruttoPrÅr = getBgBruttoPrÅr(andel);
        if (bgBruttoPrÅr != null) {
            builder.medMånedsinntekt(getMånedsinntekt(andel).longValue());
            builder.medÅrsinntekt(andel.getBruttoPrÅr().longValue());
        }

        if (AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())) {
            getArbeidsgiverNavn(andel).ifPresent(builder::medArbeidsgiverNavn);
        }

        return builder.build();
    }

    private static BigDecimal getMånedsinntekt(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::getNavn);
    }

    private static BigDecimal getBgBruttoPrÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
    }

    private static int tellAntallArbeidsforholdIBeregningUtenSluttpakke(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return (int) bgpsaListe.stream()
                .filter(bgpsa -> AktivitetStatus.ARBEIDSTAKER.equals(bgpsa.getAktivitetStatus()))
                .filter(bgpsa -> !OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(bgpsa.getArbeidsforholdType()))
                .count();
    }

    private static boolean nyoppstartetSelvstendingNæringsdrivende(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
                .filter(andel -> andel.getNyIArbeidslivet() != null)
                .map(BeregningsgrunnlagPrStatusOgAndel::getNyIArbeidslivet)
                .findFirst()
                .orElse(false);
    }

    private static List<BeregningsgrunnlagAndel> mapAndelListe(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagAndel> andeler = new ArrayList<>();
        bgpsaListe.forEach(bgpsa -> andeler.add(lagBeregningsgrunnlagAndel(bgpsa)));
        return andeler;
    }

    private static BeregningsgrunnlagPeriode finnFørstePeriode(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);
    }
}

package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class BeregningsgrunnlagMapper {

    private static final Map<AktivitetStatus, List<AktivitetStatus>> kombinerteRegelStatuserMap = new HashMap<>();

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN,
                List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    public static Beløp finnBrutto(Beregningsgrunnlag beregningsgrunnlag) {
        double sum = finnBgpsaListe(beregningsgrunnlag).stream()
                .mapToDouble(andel -> {
                    if (andel.getBruttoPrÅr() != null) {
                        return andel.getBruttoPrÅr().doubleValue();
                    } else if (andel.getAvkortetPrÅr() != null) {
                        return andel.getAvkortetPrÅr().doubleValue();
                    }
                    return 0;
                }).reduce(0, Double::sum);

        return Beløp.of(sum);
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
            builder.medAktivitetStatus(bgAktivitetStatus.aktivitetStatus().name());
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
        return beregningsgrunnlagregler.stream().anyMatch(regel -> AktivitetStatus.erKombinertStatus(regel.getRegelStatus()));
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> finnBgpsaListe(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBeregningsgrunnlagPrStatusOgAndelList();
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus,
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

            // Spesialhåndtering av tilkommet arbeidsforhold for Dagpenger og AAP - andeler
            // som ikke kan mappes gjennom aktivitetesstatuslisten på beregningsgrunnlag da
            // de er tilkommet etter skjæringstidspunkt. Typisk dersom arbeidsgiver er
            // tilkommet etter start permisjon og krever refusjon i permisjonstiden.
            List<AktivitetStatus> aktuelleStatuserForTilkommetArbForhold = List.of(AktivitetStatus.DAGPENGER,
                    AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
            if (resultatListe.stream().anyMatch(br -> aktuelleStatuserForTilkommetArbForhold.contains(br.getAktivitetStatus()))
                    && hentSummertDagsats(resultatListe) != hentSummertDagsats(bgpsaListe)) {
                long sumTilkommetDagsats = hentSumTilkommetDagsats(bgpsaListe);
                if (sumTilkommetDagsats != 0) {
                    resultatListe.forEach(rl -> {
                        if (aktuelleStatuserForTilkommetArbForhold.contains(rl.getAktivitetStatus())) {
                            rl.setDagsats(sumTilkommetDagsats);
                        }
                    });
                }
            }
        }
        if (resultatListe.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(AktivitetStatus::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.aktivitetStatus(), sb));
        }
        return resultatListe;
    }

    private static long hentSummertDagsats(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getDagsats).reduce(Long::sum).orElse(0L);
    }

    private static long hentSumTilkommetDagsats(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .filter(andel -> andel.getDagsats() > 0)
                .filter(BeregningsgrunnlagPrStatusOgAndel::getErTilkommetAndel)
                .map(BeregningsgrunnlagPrStatusOgAndel::getDagsats)
                .reduce(Long::sum).orElse(0L);
    }

    private static BeregningsgrunnlagAndel lagBeregningsgrunnlagAndel(BeregningsgrunnlagPrStatusOgAndel andel) {
        BeregningsgrunnlagAndel.Builder builder = BeregningsgrunnlagAndel.ny();

        builder.medAktivitetStatus(andel.getAktivitetStatus().name());
        builder.medDagsats(andel.getDagsats());
        builder.medEtterlønnSluttpakke(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType()));

        BigDecimal bgBruttoPrÅr = getBgBruttoPrÅr(andel);
        if (bgBruttoPrÅr != null) {
            builder.medMånedsinntekt(getMånedsinntekt(andel).longValue());
            builder.medÅrsinntekt(andel.getBruttoPrÅr().longValue());
        }
        if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())) {
            builder.medSistLignedeÅr(andel.getBeregningsperiodeTom() == null ? 0 : andel.getBeregningsperiodeTom().getYear());
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
        return andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::navn);
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

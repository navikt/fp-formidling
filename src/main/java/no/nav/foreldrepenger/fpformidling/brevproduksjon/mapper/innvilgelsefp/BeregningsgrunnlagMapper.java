package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnBgpsaListe;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnFørstePeriode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.fpformidling.domene.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

public final class BeregningsgrunnlagMapper {

    private BeregningsgrunnlagMapper() {
    }

    public static Beløp finnBrutto(Beregningsgrunnlag beregningsgrunnlag) {
        var sum = finnBgpsaListe(beregningsgrunnlag).stream().mapToDouble(andel -> {
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
        var bgpsaListe = finnBgpsaListe(beregningsgrunnlag);

        //Spesielhåndtering av militærstatus. Dersom personen har militærstatus med dagsats er beregningsgrunnlaget satt til
        //3G. I disse tilfellene skal andre statuser ignoreres (Beregning fjerner ikke andre statuser). Gjelder både FP og SVP.
        if (harMilitærStatusMedDagsatsOgAnnenStatus(bgpsaListe)) {
            beregningsgrunnlagregler.add(
                opprettBeregningsregel(bgpsaListe, new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.MILITÆR_ELLER_SIVIL)));
        } else {
            for (var bgAktivitetStatus : beregningsgrunnlag.getAktivitetStatuser()) {
                beregningsgrunnlagregler.add(opprettBeregningsregel(bgpsaListe, bgAktivitetStatus));
            }
        }
        return beregningsgrunnlagregler;
    }

    private static BeregningsgrunnlagRegel opprettBeregningsregel(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe,
                                                                  BeregningsgrunnlagAktivitetStatus bgAktivitetStatus) {
        var builder = BeregningsgrunnlagRegel.ny();
        var filtrertListe = finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe);
        builder.medAktivitetStatus(utledAktivitetStatus(bgAktivitetStatus.aktivitetStatus(), bgpsaListe));
        builder.medAndelListe(mapAndelListe(filtrertListe));
        builder.medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(tellAntallArbeidsforholdIBeregningUtenSluttpakke(filtrertListe));
        builder.medSnNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
        return builder.build();
    }

    private static String utledAktivitetStatus(AktivitetStatus aktivitetStatus, List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        if (erAAPUnderArbeid(bgpsaListe, aktivitetStatus)) {
            return AktivitetStatus.ARBEID_UNDER_AAP.name();
        } else {
            return aktivitetStatus.name();
        }
    }

    private static boolean erAAPUnderArbeid(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe, AktivitetStatus aktivitetStatus) {
    return bgpsaListe.stream()
        .filter(andel -> andel.getAktivitetStatus().equals(aktivitetStatus))
        .anyMatch(andel-> OpptjeningAktivitetType.ARBEID_UNDER_AAP.equals(andel.getArbeidsforholdType()));
    }

    private static boolean harMilitærStatusMedDagsatsOgAnnenStatus(List<BeregningsgrunnlagPrStatusOgAndel> andeler) {
        var harMilitærAndelerMedDagsats = andeler.stream()
            .filter(status -> AktivitetStatus.MILITÆR_ELLER_SIVIL.equals(status.getAktivitetStatus()))
            .anyMatch(andel -> andel.getDagsats() > 0);

        if (!harMilitærAndelerMedDagsats) {
            return false;
        } else {
            return !andeler.stream().allMatch(status -> AktivitetStatus.MILITÆR_ELLER_SIVIL.equals(status.getAktivitetStatus()));
        }
    }

    public static boolean harBruktBruttoBeregningsgrunnlag(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return (beregningsgrunnlagregler.size() > 1 || (minstEnRegelHarKombinertAktivitetStatus(beregningsgrunnlagregler)));
    }

    private static boolean minstEnRegelHarKombinertAktivitetStatus(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return beregningsgrunnlagregler.stream().anyMatch(regel -> AktivitetStatus.erKombinertStatus(regel.getRegelStatus()));
    }

    private static BeregningsgrunnlagAndel lagBeregningsgrunnlagAndel(BeregningsgrunnlagPrStatusOgAndel andel) {
        var builder = BeregningsgrunnlagAndel.ny();

        builder.medAktivitetStatus(andel.getAktivitetStatus().name());

        BigDecimal ikkeRedusertDagsatsAAP = null;
        //Dersom andelen er arbeidsavklaringspenger, kan dagsatsen være redusert hvis bruker har 80% foreldrepenger, men det er brutto dagsats som skal vises i brevet under beregningskapittelet
        if (AktivitetStatus.ARBEIDSAVKLARINGSPENGER.equals(andel.getAktivitetStatus())) {
            var bgBruttoPrÅr = andel.getBruttoPrÅr();
            ikkeRedusertDagsatsAAP = bgBruttoPrÅr != null && bgBruttoPrÅr.compareTo(BigDecimal.ZERO) > 0 ? bgBruttoPrÅr.divide(BigDecimal.valueOf(260), 0, RoundingMode.HALF_UP) : null;
        }
        builder.medDagsats(ikkeRedusertDagsatsAAP != null ? ikkeRedusertDagsatsAAP.longValue() : andel.getDagsats());
        builder.medEtterlønnSluttpakke(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType()));
        builder.medMånedsinntekt(getMånedsinntekt(andel).longValue());
        builder.medÅrsinntekt(andel.getBruttoPrÅr().longValue());

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

    private static int tellAntallArbeidsforholdIBeregningUtenSluttpakke(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return (int) bgpsaListe.stream()
            .filter(bgpsa -> AktivitetStatus.ARBEIDSTAKER.equals(bgpsa.getAktivitetStatus()))
            .filter(bgpsa -> !OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(bgpsa.getArbeidsforholdType()))
            .count();
    }

    private static boolean nyoppstartetSelvstendingNæringsdrivende(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
            .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
            .map(BeregningsgrunnlagPrStatusOgAndel::getNyIArbeidslivet)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(false);
    }

    private static List<BeregningsgrunnlagAndel> mapAndelListe(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagAndel> andeler = new ArrayList<>();
        bgpsaListe.forEach(bgpsa -> andeler.add(lagBeregningsgrunnlagAndel(bgpsa)));
        return andeler;
    }
}

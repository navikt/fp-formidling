package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnFørstePeriode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;

public final class BeregningsgrunnlagMapper {

    private BeregningsgrunnlagMapper() {
    }

    public static Beløp finnBrutto(BeregningsgrunnlagDto beregningsgrunnlag) {
        var sum = finnFørstePeriode(beregningsgrunnlag).beregningsgrunnlagandeler().stream().mapToDouble(andel -> {
            if (andel.bruttoPrÅr() != null) {
                return andel.bruttoPrÅr().doubleValue();
            } else if (andel.avkortetPrÅr() != null) {
                return andel.avkortetPrÅr().doubleValue();
            }
            return 0;
        }).reduce(0, Double::sum);

        return Beløp.of(sum);
    }

    public static BigDecimal finnSeksG(BeregningsgrunnlagDto beregningsgrunnlag) {
        return beregningsgrunnlag.grunnbeløp().multiply(BigDecimal.valueOf(6));
    }

    public static boolean inntektOverSeksG(BeregningsgrunnlagDto beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).bruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    public static List<BeregningsgrunnlagRegel> mapRegelListe(BeregningsgrunnlagDto beregningsgrunnlag, UnaryOperator<String> hentNavn) {
        List<BeregningsgrunnlagRegel> beregningsgrunnlagregler = new ArrayList<>();
        var bgpsaListe = finnFørstePeriode(beregningsgrunnlag).beregningsgrunnlagandeler();

        //Spesielhåndtering av militærstatus. Dersom personen har militærstatus med dagsats er beregningsgrunnlaget satt til
        //3G. I disse tilfellene skal andre statuser ignoreres (Beregning fjerner ikke andre statuser). Gjelder både FP og SVP.
        if (harMilitærStatusMedDagsatsOgAnnenStatus(bgpsaListe)) {
            beregningsgrunnlagregler.add(
                opprettBeregningsregel(bgpsaListe, AktivitetStatusDto.MILITÆR_ELLER_SIVIL, hentNavn));
        } else {
            for (var bgAktivitetStatus : beregningsgrunnlag.aktivitetstatusListe()) {
                beregningsgrunnlagregler.add(opprettBeregningsregel(bgpsaListe, bgAktivitetStatus, hentNavn));
            }
        }
        return beregningsgrunnlagregler;
    }

    private static BeregningsgrunnlagRegel opprettBeregningsregel(List<BeregningsgrunnlagAndelDto> andeler,
                                                                  AktivitetStatusDto bgAktivitetStatus,
                                                                  UnaryOperator<String> hentNavn) {
        var builder = BeregningsgrunnlagRegel.ny();
        var filtrertListe = finnAktivitetStatuserForAndeler(bgAktivitetStatus, andeler);
        builder.medAktivitetStatus(tilString(bgAktivitetStatus));
        var mapped = filtrertListe.stream().map(a -> lagBeregningsgrunnlagAndel(a, hentNavn)).toList();
        builder.medAndelListe(mapped);
        builder.medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(tellAntallArbeidsforholdIBeregningUtenSluttpakke(filtrertListe));
        builder.medSnNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
        return builder.build();
    }

    private static String tilString(AktivitetStatusDto bgAktivitetStatus) {
        return switch (bgAktivitetStatus) {
            case ARBEIDSAVKLARINGSPENGER -> "ARBEIDSAVKLARINGSPENGER";
            case ARBEIDSTAKER -> "ARBEIDSTAKER";
            case BRUKERS_ANDEL -> "BRUKERS_ANDEL";
            case DAGPENGER -> "DAGPENGER";
            case FRILANSER -> "FRILANSER";
            case KOMBINERT_AT_FL -> "KOMBINERT_AT_FL";
            case KOMBINERT_AT_FL_SN -> "KOMBINERT_AT_FL_SN";
            case KOMBINERT_AT_SN -> "KOMBINERT_AT_SN";
            case KOMBINERT_FL_SN -> "KOMBINERT_FL_SN";
            case KUN_YTELSE -> "KUN_YTELSE";
            case MILITÆR_ELLER_SIVIL -> "MILITÆR_ELLER_SIVIL";
            case SELVSTENDIG_NÆRINGSDRIVENDE -> "SELVSTENDIG_NÆRINGSDRIVENDE";
            case TTLSTØTENDE_YTELSE -> "TTLSTØTENDE_YTELSE";
            case VENTELØNN_VARTPENGER -> "VENTELØNN_VARTPENGER";
        };
    }

    private static boolean harMilitærStatusMedDagsatsOgAnnenStatus(List<BeregningsgrunnlagAndelDto> andeler) {
        var harMilitærAndelerMedDagsats = andeler.stream()
            .filter(status -> AktivitetStatusDto.MILITÆR_ELLER_SIVIL.equals(status.aktivitetStatus()))
            .anyMatch(andel -> andel.dagsats() > 0);

        if (!harMilitærAndelerMedDagsats) {
            return false;
        } else {
            return !andeler.stream().allMatch(status -> AktivitetStatusDto.MILITÆR_ELLER_SIVIL.equals(status.aktivitetStatus()));
        }
    }

    public static boolean harBruktBruttoBeregningsgrunnlag(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return (beregningsgrunnlagregler.size() > 1 || (minstEnRegelHarKombinertAktivitetStatus(beregningsgrunnlagregler)));
    }

    private static boolean minstEnRegelHarKombinertAktivitetStatus(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
        return beregningsgrunnlagregler.stream().anyMatch(regel -> AktivitetStatus.erKombinertStatus(regel.getRegelStatus()));
    }

    public static BeregningsgrunnlagAndel lagBeregningsgrunnlagAndel(BeregningsgrunnlagAndelDto andel, UnaryOperator<String> hentNavn) {
        var builder = BeregningsgrunnlagAndel.ny();

        builder.medAktivitetStatus(andel.aktivitetStatus().name());

        BigDecimal ikkeRedusertDagsatsAAP = null;
        //Dersom andelen er arbeidsavklaringspenger, kan dagsatsen være redusert hvis bruker har 80% foreldrepenger, men det er brutto dagsats som skal vises i brevet under beregningskapittelet
        if (AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER.equals(andel.aktivitetStatus())) {
            var bgBruttoPrÅr = andel.bruttoPrÅr();
            ikkeRedusertDagsatsAAP = bgBruttoPrÅr != null && bgBruttoPrÅr.compareTo(BigDecimal.ZERO) > 0 ? bgBruttoPrÅr.divide(BigDecimal.valueOf(260), 0, RoundingMode.HALF_UP) : null;
        }
        builder.medDagsats(ikkeRedusertDagsatsAAP != null ? ikkeRedusertDagsatsAAP.longValue() : andel.dagsats());
        builder.medEtterlønnSluttpakke(OpptjeningAktivitetDto.ETTERLØNN_SLUTTPAKKE.equals(andel.arbeidsforholdType()));
        builder.medMånedsinntekt(getMånedsinntekt(andel).longValue());
        builder.medÅrsinntekt(andel.bruttoPrÅr().longValue());

        if (AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.aktivitetStatus())) {
            builder.medSistLignedeÅr(andel.beregningsperiodeTom() == null ? 0 : andel.beregningsperiodeTom().getYear());
        }
        if (AktivitetStatusDto.ARBEIDSTAKER.equals(andel.aktivitetStatus())) {
            getArbeidsgiverNavn(andel, hentNavn).ifPresent(builder::medArbeidsgiverNavn);
        }

        return builder.build();
    }

    private static BigDecimal getMånedsinntekt(BeregningsgrunnlagAndelDto andel) {
        return andel.bruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagAndelDto andel, UnaryOperator<String> hentNavn) {
        return Optional.ofNullable(andel.arbeidsforhold()).map(BgAndelArbeidsforholdDto::arbeidsgiverIdent).map(hentNavn);
    }

    private static int tellAntallArbeidsforholdIBeregningUtenSluttpakke(List<BeregningsgrunnlagAndelDto> bgpsaListe) {
        return (int) bgpsaListe.stream()
            .filter(bgpsa -> AktivitetStatusDto.ARBEIDSTAKER.equals(bgpsa.aktivitetStatus()))
            .filter(bgpsa -> !OpptjeningAktivitetDto.ETTERLØNN_SLUTTPAKKE.equals(bgpsa.arbeidsforholdType()))
            .count();
    }

    private static boolean nyoppstartetSelvstendingNæringsdrivende(List<BeregningsgrunnlagAndelDto> bgpsaListe) {
        return bgpsaListe.stream()
            .filter(andel -> AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.aktivitetStatus()))
            .map(BeregningsgrunnlagAndelDto::erNyIArbeidslivet)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(false);
    }
}

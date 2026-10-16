package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;

public final class BeregningsgrunnlagMapper {

    private static final Map<AktivitetStatusDto, List<AktivitetStatusDto>> KOMBINERTE_REGEL_STATUSER_MAP = new EnumMap<>(AktivitetStatusDto.class);

    static {
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatusDto.KOMBINERT_AT_FL, List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.FRILANSER));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatusDto.KOMBINERT_AT_SN,
            List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatusDto.KOMBINERT_AT_FL_SN,
            List.of(AktivitetStatusDto.ARBEIDSTAKER, AktivitetStatusDto.FRILANSER, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatusDto.KOMBINERT_FL_SN,
            List.of(AktivitetStatusDto.FRILANSER, AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    private BeregningsgrunnlagMapper() {
    }

    public static List<BeregningsgrunnlagAndelDto> finnAktivitetStatuserForAndelerOgFjernTilkommet(AktivitetStatusDto bgAktivitetStatus,
                                                                                                   List<BeregningsgrunnlagAndelDto> andeler) {
        if (AktivitetStatusDto.KUN_YTELSE.equals(bgAktivitetStatus)) {
            return andeler;
        }

        var relevanteStatuser = erKombinertStatus(bgAktivitetStatus)
            ? KOMBINERTE_REGEL_STATUSER_MAP.get(bgAktivitetStatus)
            : List.of(bgAktivitetStatus);
        //Tilkommet andeler er ikke en del av beregningen
        var resultatListe = andeler.stream()
            .filter(andel -> !andel.erTilkommetAndel())
            .filter(andel -> relevanteStatuser.contains(andel.aktivitetStatus()))
            .toList();

        if (resultatListe.isEmpty()) {
            var sb = new StringBuilder();
            andeler.stream().map(BeregningsgrunnlagAndelDto::aktivitetStatus).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus, sb));
        }

        return resultatListe;
    }

    public static boolean erKombinertStatus(AktivitetStatusDto as) {
        return Set.of(AktivitetStatusDto.KOMBINERT_AT_FL_SN, AktivitetStatusDto.KOMBINERT_AT_FL, AktivitetStatusDto.KOMBINERT_AT_SN,
            AktivitetStatusDto.KOMBINERT_FL_SN).contains(as);
    }

    public static long getHalvGOrElseZero(Optional<BeregningsgrunnlagDto> beregningsgrunnlag) {
        return beregningsgrunnlag.map(BeregningsgrunnlagDto::grunnbeløp)
            .orElse(BigDecimal.ZERO)
            .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
            .longValue();
    }

    public static BeregningsgrunnlagPeriodeDto finnFørstePeriode(BeregningsgrunnlagDto beregningsgrunnlag) {
        return beregningsgrunnlag.beregningsgrunnlagperioder().getFirst();
    }

    public static BigDecimal getMånedsinntekt(BeregningsgrunnlagAndelDto andel) {
        return getÅrsinntekt(andel).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    public static BigDecimal getÅrsinntekt(BeregningsgrunnlagAndelDto andel) {
        //gjeldendeGrunnlagPrÅr inneholder beregnet grunnlag per år (av fpsak), eller det som er fastsatt av saksbehandler (overstyrtPerÅr)
        if (andel.gjeldendeGrunnlagPrÅr() == null && andel.bruttoPrÅr() == null) {
            throw new IllegalStateException("BeregningsgrunnlagAndelDto mangler både gjeldendeGrunnlagPrÅr og bruttoPrÅr, kan ikke beregne årsinntekt");
        }
        return andel.gjeldendeGrunnlagPrÅr() != null ? andel.gjeldendeGrunnlagPrÅr() : andel.bruttoPrÅr();
    }
}

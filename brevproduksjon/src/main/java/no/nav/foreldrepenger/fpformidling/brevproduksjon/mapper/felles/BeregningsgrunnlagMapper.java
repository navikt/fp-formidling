package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;

public final class BeregningsgrunnlagMapper {

    private static final Map<AktivitetStatus, List<AktivitetStatus>> KOMBINERTE_REGEL_STATUSER_MAP = new EnumMap<>(AktivitetStatus.class);

    static {
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatus.KOMBINERT_AT_SN,
            List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatus.KOMBINERT_AT_FL_SN,
            List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        KOMBINERTE_REGEL_STATUSER_MAP.put(AktivitetStatus.KOMBINERT_FL_SN,
            List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    private BeregningsgrunnlagMapper() {
    }

    public static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus,
                                                                                          List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagPrStatusOgAndel> resultatListe;

        if (AktivitetStatus.KUN_YTELSE.equals(bgAktivitetStatus.aktivitetStatus())) {
            return bgpsaListe;
        }

        if (bgAktivitetStatus.aktivitetStatus().harKombinertStatus()) {
            var relevanteStatuser = KOMBINERTE_REGEL_STATUSER_MAP.get(bgAktivitetStatus.aktivitetStatus());
            resultatListe = bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).toList();
        } else {
            resultatListe = bgpsaListe.stream().filter(andel -> bgAktivitetStatus.aktivitetStatus().equals(andel.getAktivitetStatus())).toList();

            // Spesialhåndtering av tilkommet arbeidsforhold for Dagpenger og AAP - andeler som ikke kan mappes gjennom
            // aktivitetesstatuslisten på beregningsgrunnlag da de er tilkommet etter skjæringstidspunkt. Typisk dersom arbeidsgiver er
            // tilkommet etter start permisjon og krever refusjon i permisjonstiden.
            var aktuelleStatuserForTilkommetArbForhold = List.of(AktivitetStatus.DAGPENGER, AktivitetStatus.ARBEIDSAVKLARINGSPENGER);

            if (resultatListe.stream().anyMatch(br -> aktuelleStatuserForTilkommetArbForhold.contains(br.getAktivitetStatus()))
                && hentSummertDagsats(resultatListe) != hentSummertDagsats(bgpsaListe)) {
                var sumTilkommetDagsats = hentSumTilkommetDagsats(bgpsaListe);
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
            var sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(AktivitetStatus::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.aktivitetStatus(), sb));
        }
        return resultatListe;
    }

    public static long getHalvGOrElseZero(Optional<Beregningsgrunnlag> beregningsgrunnlag) {
        return beregningsgrunnlag.map(Beregningsgrunnlag::getGrunnbeløp)
            .map(Beløp::getVerdi)
            .orElse(BigDecimal.ZERO)
            .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
            .longValue();
    }

    private static long hentSummertDagsats(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getDagsats).reduce(Long::sum).orElse(0L);
    }

    private static long hentSumTilkommetDagsats(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
            .filter(andel -> andel.getDagsats() > 0)
            .filter(BeregningsgrunnlagPrStatusOgAndel::getErTilkommetAndel)
            .map(BeregningsgrunnlagPrStatusOgAndel::getDagsats)
            .reduce(Long::sum)
            .orElse(0L);
    }

    public static List<BeregningsgrunnlagPrStatusOgAndel> finnBgpsaListe(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBeregningsgrunnlagPrStatusOgAndelList();
    }

    public static BeregningsgrunnlagPeriode finnFørstePeriode(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);
    }

}

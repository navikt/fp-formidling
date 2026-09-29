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

    public static List<BeregningsgrunnlagAndelDto> finnAktivitetStatuserForAndeler(AktivitetStatusDto bgAktivitetStatus,
                                                                                   List<BeregningsgrunnlagAndelDto> andeler) {
        List<BeregningsgrunnlagAndelDto> resultatListe;

        if (AktivitetStatusDto.KUN_YTELSE.equals(bgAktivitetStatus)) {
            return andeler;
        }

        if (erKombinertStatus(bgAktivitetStatus)) {
            var relevanteStatuser = KOMBINERTE_REGEL_STATUSER_MAP.get(bgAktivitetStatus);
            resultatListe = andeler.stream().filter(andel -> relevanteStatuser.contains(andel.aktivitetStatus())).toList();
        } else {
            resultatListe = andeler.stream().filter(andel -> bgAktivitetStatus.equals(andel.aktivitetStatus())).toList();


            // Spesialhåndtering av tilkommet arbeidsforhold for Dagpenger og AAP - andeler som ikke kan mappes gjennom
            // aktivitetesstatuslisten på beregningsgrunnlag da de er tilkommet etter skjæringstidspunkt. Typisk dersom arbeidsgiver er
            // tilkommet etter start permisjon og krever refusjon i permisjonstiden.
            var aktuelleStatuserForTilkommetArbForhold = List.of(AktivitetStatusDto.DAGPENGER, AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER);
            if (resultatListe.stream().map(BeregningsgrunnlagAndelDto::aktivitetStatus).anyMatch(aktuelleStatuserForTilkommetArbForhold::contains)
                && hentSummertDagsats(resultatListe) != hentSummertDagsatsDto(andeler)) {
                var sumTilkommetDagsats = hentSumTilkommetDagsats(andeler);
                if (sumTilkommetDagsats != 0) {
                    resultatListe = resultatListe.stream().map(rl -> {
                        if (aktuelleStatuserForTilkommetArbForhold.contains(rl.aktivitetStatus())) {
                            var nyDagsats = rl.dagsats() + sumTilkommetDagsats;
                            return kopiMedNyDagsats(rl, nyDagsats);
                        }
                        return rl;
                    }).toList();
                }
            }
        }

        if (resultatListe.isEmpty()) {
            var sb = new StringBuilder();
            andeler.stream().map(BeregningsgrunnlagAndelDto::aktivitetStatus).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus, sb));
        }
        return resultatListe;
    }

    private static BeregningsgrunnlagAndelDto kopiMedNyDagsats(BeregningsgrunnlagAndelDto original, long nyDagsats) {
        return new BeregningsgrunnlagAndelDto(nyDagsats, original.aktivitetStatus(), original.bruttoPrÅr(), original.avkortetPrÅr(),
            original.erNyIArbeidslivet(), original.arbeidsforholdType(), original.beregningsperiodeFom(), original.beregningsperiodeTom(),
            original.arbeidsforhold(), original.erTilkommetAndel());
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

    private static long hentSummertDagsats(List<BeregningsgrunnlagAndelDto> andeler) {
        return andeler.stream().map(BeregningsgrunnlagAndelDto::dagsats).reduce(Long::sum).orElse(0L);
    }

    private static long hentSummertDagsatsDto(List<BeregningsgrunnlagAndelDto> andeler) {
        return andeler.stream().map(BeregningsgrunnlagAndelDto::dagsats).reduce(Long::sum).orElse(0L);
    }

    private static long hentSumTilkommetDagsats(List<BeregningsgrunnlagAndelDto> andeler) {
        return andeler.stream()
            .filter(andel -> andel.dagsats() > 0)
            .filter(BeregningsgrunnlagAndelDto::erTilkommetAndel)
            .map(BeregningsgrunnlagAndelDto::dagsats)
            .reduce(Long::sum)
            .orElse(0L);
    }

    public static BeregningsgrunnlagPeriodeDto finnFørstePeriode(BeregningsgrunnlagDto beregningsgrunnlag) {
        return beregningsgrunnlag.beregningsgrunnlagperioder().getFirst();
    }

}

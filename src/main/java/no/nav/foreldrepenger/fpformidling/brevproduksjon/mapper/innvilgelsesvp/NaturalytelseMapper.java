package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.finnBeregningsgrunnlagperiode;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

public final class NaturalytelseMapper {

    private NaturalytelseMapper() {
    }

    public static List<Naturalytelse> mapNaturalytelser(TilkjentYtelseDagytelseDto tilkjentYtelse,
                                                        BeregningsgrunnlagDto beregningsgrunnlag,
                                                        Språkkode språkkode,
                                                        UnaryOperator<String> hentNavn) {
        var naturalytelser = new TreeSet<>(Comparator.comparing(Naturalytelse::getEndringsdatoDate));

        var beregningingsgrunnlagperioder = beregningsgrunnlag.beregningsgrunnlagperioder();
        var startFørstePeriode = finnStartFørstePeriode(beregningingsgrunnlagperioder);
        tilkjentYtelse.perioder().forEach(tilkjentYtelsePeriode -> {
            var matchetBgPeriode = finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode, beregningingsgrunnlagperioder);
            tilkjentYtelsePeriode.andeler().forEach(andel -> {
                if (harNaturalytelse(matchetBgPeriode, andel, startFørstePeriode)) {
                    opprettNaturalytelse(matchetBgPeriode, andel, språkkode, hentNavn).ifPresent(naturalytelser::add);
                }
            });
        });

        return naturalytelser.stream().toList();
    }

    private static LocalDate finnStartFørstePeriode(List<BeregningsgrunnlagPeriodeDto> beregningingsgrunnlagperioder) {
        return beregningingsgrunnlagperioder.stream()
            .map(BeregningsgrunnlagPeriodeDto::beregningsgrunnlagperiodeFom)
            .toList()
            .stream()
            .min(Comparator.naturalOrder())
            .orElse(null);
    }

    private static boolean harNaturalytelse(BeregningsgrunnlagPeriodeDto matchetBgPeriode, TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto andel, LocalDate startFørstePeriode) {
        return PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(matchetBgPeriode.beregningsgrunnlagandeler(), andel)
            .flatMap(a -> Optional.ofNullable(a.arbeidsforhold()))
            .filter(bgAndelArbeidsforhold -> bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null
                || bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null)
            .isPresent() && ikkeErFørstePeriode(startFørstePeriode, matchetBgPeriode);
    }

    private static boolean ikkeErFørstePeriode(LocalDate startFørstePeriode, BeregningsgrunnlagPeriodeDto matchetBgPeriode) {
        return startFørstePeriode != null && !startFørstePeriode.equals(matchetBgPeriode.beregningsgrunnlagperiodeFom());
    }

    private static Optional<Naturalytelse> opprettNaturalytelse(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                                TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto andel,
                                                                Språkkode språkkode,
                                                                UnaryOperator<String> hentNavn) {
        Optional<Naturalytelse> naturalytelse = Optional.empty();
        var naturalytelseStatus = utledNaturalytelseStatus(beregningsgrunnlagPeriode);

        if (naturalytelseStatus != null) {
            naturalytelse = Optional.of(Naturalytelse.ny()
                .medStatus(naturalytelseStatus)
                .medEndringsdato(beregningsgrunnlagPeriode.beregningsgrunnlagperiodeFom(), språkkode)
                .medNyDagsats(beregningsgrunnlagPeriode.dagsats())
                .medArbeidsgiverNavn(utledAktivitetsbeskrivelse(andel, andel.aktivitetstatus(), hentNavn))
                .build());
        }
        return naturalytelse;
    }

    private static NaturalytelseStatus utledNaturalytelseStatus(BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode) {
        NaturalytelseStatus naturalytelseStatus = null;

        for (var årsak : beregningsgrunnlagPeriode.periodeårsaker()) {
            if (PeriodeÅrsakDto.NATURALYTELSE_BORTFALT.equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.BORTFALLER;
            } else if (PeriodeÅrsakDto.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.TILKOMMER;
            }
        }

        if (naturalytelseStatus != null) {
            return naturalytelseStatus;
        }

        for (var andel : beregningsgrunnlagPeriode.beregningsgrunnlagandeler()) {
            var arbeidsforhold = Optional.ofNullable(andel.arbeidsforhold());
            var bortfaltPrÅr = arbeidsforhold.map(BgAndelArbeidsforholdDto::naturalytelseBortfaltPrÅr).orElse(null);
            var tilkommetPrÅr = arbeidsforhold.map(BgAndelArbeidsforholdDto::naturalytelseTilkommetPrÅr).orElse(null);
            if ((bortfaltPrÅr != null && tilkommetPrÅr == null) || (beggeHarVerdi(bortfaltPrÅr, tilkommetPrÅr)
                && tilkommetPrÅr.compareTo(bortfaltPrÅr) < 0)) {
                naturalytelseStatus = NaturalytelseStatus.BORTFALLER;
            } else if ((bortfaltPrÅr == null && tilkommetPrÅr != null) || (beggeHarVerdi(bortfaltPrÅr, tilkommetPrÅr)
                && tilkommetPrÅr.compareTo(bortfaltPrÅr) >= 0)) {
                naturalytelseStatus = NaturalytelseStatus.TILKOMMER;
            }
        }
        return naturalytelseStatus;
    }

    private static boolean beggeHarVerdi(BigDecimal bortfaltPrÅr, BigDecimal tilkommetPrÅr) {
        return bortfaltPrÅr != null && tilkommetPrÅr != null;
    }
}

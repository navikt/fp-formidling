package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.finnBeregningsgrunnlagperiode;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;

public final class NaturalytelseMapper {

    public static List<Naturalytelse> mapNaturalytelser(TilkjentYtelseForeldrepenger tilkjentYtelse,
                                                        Beregningsgrunnlag beregningsgrunnlag,
                                                        Språkkode språkkode) {
        var naturalytelser = new TreeSet<Naturalytelse>(Comparator.comparing(Naturalytelse::getEndringsdatoDate));

        var beregningingsgrunnlagperioder = beregningsgrunnlag.getBeregningsgrunnlagPerioder();
        var startFørstePeriode = finnStartFørstePeriode(beregningingsgrunnlagperioder);
        tilkjentYtelse.getPerioder().forEach(tilkjentYtelsePeriode -> {
            var matchetBgPeriode = finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode, beregningingsgrunnlagperioder);
            tilkjentYtelsePeriode.getAndeler().forEach(andel -> {
                if (harNaturalytelse(matchetBgPeriode, andel, startFørstePeriode)) {
                    opprettNaturalytelse(matchetBgPeriode, andel, språkkode).ifPresent(naturalytelser::add);
                }
            });
        });

        return naturalytelser.stream().toList();
    }

    private static LocalDate finnStartFørstePeriode(List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder) {
        return beregningingsgrunnlagperioder.stream()
                .map(BeregningsgrunnlagPeriode::getBeregningsgrunnlagPeriodeFom).toList().stream().min(Comparator.naturalOrder()).orElse(null);
    }

    private static boolean harNaturalytelse(BeregningsgrunnlagPeriode matchetBgPeriode, TilkjentYtelseAndel andel, LocalDate startFørstePeriode) {
        return PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(matchetBgPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)
                .flatMap(BeregningsgrunnlagPrStatusOgAndel::getBgAndelArbeidsforhold)
                .filter(bgAndelArbeidsforhold -> bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null
                        || bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null)
                .isPresent() && ikkeErFørstePeriode(startFørstePeriode, matchetBgPeriode);
    }

    private static boolean ikkeErFørstePeriode(LocalDate startFørstePeriode, BeregningsgrunnlagPeriode matchetBgPeriode) {
        return startFørstePeriode != null && !startFørstePeriode.equals(matchetBgPeriode.getBeregningsgrunnlagPeriodeFom());
    }

    private static Optional<Naturalytelse> opprettNaturalytelse(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                                TilkjentYtelseAndel andel, Språkkode språkkode) {
        Optional<Naturalytelse> naturalytelse = Optional.empty();
        var naturalytelseStatus = utledNaturalytelseStatus(beregningsgrunnlagPeriode);

        if (naturalytelseStatus != null) {
            naturalytelse = Optional.of(Naturalytelse.ny()
                    .medStatus(naturalytelseStatus)
                    .medEndringsdato(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeFom(), språkkode)
                    .medNyDagsats(beregningsgrunnlagPeriode.getDagsats())
                    .medArbeidsgiverNavn(utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus()))
                    .build());
        }
        return naturalytelse;
    }

    private static NaturalytelseStatus utledNaturalytelseStatus(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        NaturalytelseStatus naturalytelseStatus = null;

        for (var årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.BORTFALLER;
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.TILKOMMER;
            }
        }

        if (naturalytelseStatus != null) {
            return naturalytelseStatus;
        }

        for (var andel : beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList()) {
            var bortfaltPrÅr = andel.getBgAndelArbeidsforhold().map(BGAndelArbeidsforhold::getNaturalytelseBortfaltPrÅr).orElse(null);
            var tilkommetPrÅr = andel.getBgAndelArbeidsforhold().map(BGAndelArbeidsforhold::getNaturalytelseTilkommetPrÅr).orElse(null);
            if ((bortfaltPrÅr != null && tilkommetPrÅr == null) ||
                    (beggeHarVerdi(bortfaltPrÅr, tilkommetPrÅr) && tilkommetPrÅr.compareTo(bortfaltPrÅr) < 0)) {
                naturalytelseStatus = NaturalytelseStatus.BORTFALLER;
            } else if ((bortfaltPrÅr == null && tilkommetPrÅr != null) ||
                    (beggeHarVerdi(bortfaltPrÅr, tilkommetPrÅr) && tilkommetPrÅr.compareTo(bortfaltPrÅr) >= 0)) {
                naturalytelseStatus = NaturalytelseStatus.TILKOMMER;
            }
        }
        return naturalytelseStatus;
    }

    private static boolean beggeHarVerdi(BigDecimal bortfaltPrÅr, BigDecimal tilkommetPrÅr) {
        return bortfaltPrÅr != null && tilkommetPrÅr != null;
    }
}

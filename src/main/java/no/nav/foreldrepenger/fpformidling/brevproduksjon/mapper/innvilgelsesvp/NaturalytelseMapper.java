package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.finnBeregningsgrunnlagperiode;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

public final class NaturalytelseMapper {

    private NaturalytelseMapper() {
    }

    public static List<Naturalytelse> mapNaturalytelser(TilkjentYtelseDagytelseDto tilkjentYtelse,
                                                        BeregningsgrunnlagDto beregningsgrunnlag,
                                                        Språkkode språkkode,
                                                        ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        var naturalytelser = new TreeSet<>(Comparator.comparing(Naturalytelse::getEndringsdatoDate));

        var beregningingsgrunnlagperioder = beregningsgrunnlag.beregningsgrunnlagperioder();
        var startFørstePeriode = finnStartFørstePeriode(beregningingsgrunnlagperioder);
        tilkjentYtelse.perioder().forEach(tilkjentYtelsePeriode -> {
            var matchetBgPeriode = finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode, beregningingsgrunnlagperioder);
            tilkjentYtelsePeriode.andeler().forEach(andel -> {
                if (harNaturalytelse(matchetBgPeriode, andel, startFørstePeriode)) {
                    opprettNaturalytelse(matchetBgPeriode, andel, språkkode, arbeidsgiverTjeneste).ifPresent(naturalytelser::add);
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

    private static Optional<Naturalytelse> opprettNaturalytelse(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                                TilkjentYtelseAndel andel,
                                                                Språkkode språkkode,
                                                                ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        Optional<Naturalytelse> naturalytelse = Optional.empty();
        var naturalytelseStatus = utledNaturalytelseStatus(beregningsgrunnlagPeriode);

        if (naturalytelseStatus != null) {
            naturalytelse = Optional.of(Naturalytelse.ny()
                .medStatus(naturalytelseStatus)
                .medEndringsdato(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeFom(), språkkode)
                .medNyDagsats(beregningsgrunnlagPeriode.getDagsats())
                .medArbeidsgiverNavn(utledAktivitetsbeskrivelse(andel, andel.getAktivitetStatus(), arbeidsgiverTjeneste))
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

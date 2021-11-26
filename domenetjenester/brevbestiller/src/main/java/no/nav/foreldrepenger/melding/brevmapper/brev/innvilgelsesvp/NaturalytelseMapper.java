package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.finnBeregningsgrunnlagperiode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Naturalytelse.NaturalytelseStatus;

public final class NaturalytelseMapper {

    public static List<Naturalytelse> mapNaturalytelser(BeregningsresultatFP beregningsresultat,
                                                        Beregningsgrunnlag beregningsgrunnlag,
                                                        Språkkode språkkode) {
        TreeSet<Naturalytelse> naturalytelser = new TreeSet<>(Comparator.comparing(Naturalytelse::getEndringsdatoDate));

        List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder = beregningsgrunnlag.getBeregningsgrunnlagPerioder();
        LocalDate startFørstePeriode = finnStartFørstePeriode(beregningingsgrunnlagperioder);
        beregningsresultat.getBeregningsresultatPerioder().forEach(beregningsresultatPeriode -> {
            var matchetBgPeriode = finnBeregningsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
            beregningsresultatPeriode.getBeregningsresultatAndelList().forEach(andel -> {
                if (harNaturalytelse(matchetBgPeriode, andel, startFørstePeriode)) {
                    opprettNaturalytelse(matchetBgPeriode, andel, språkkode).ifPresent(naturalytelser::add);
                }
            });
        });

        return naturalytelser.stream().toList();
    }

    private static LocalDate finnStartFørstePeriode(List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder) {
        return beregningingsgrunnlagperioder.stream()
                .map(BeregningsgrunnlagPeriode::getBeregningsgrunnlagPeriodeFom)
                .collect(Collectors.toList()).stream().min(Comparator.naturalOrder()).orElse(null);
    }

    private static boolean harNaturalytelse(BeregningsgrunnlagPeriode matchetBgPeriode, BeregningsresultatAndel andel, LocalDate startFørstePeriode) {
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
                                                                BeregningsresultatAndel andel, Språkkode språkkode) {
        Optional<Naturalytelse> naturalytelse = Optional.empty();
        NaturalytelseStatus naturalytelseStatus = utledNaturalytelseStatus(beregningsgrunnlagPeriode);

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

        for (String årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode().equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.BORTFALLER;
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.getKode().equals(årsak)) {
                naturalytelseStatus = NaturalytelseStatus.TILKOMMER;
            }
        }

        if (naturalytelseStatus != null) {
            return naturalytelseStatus;
        }

        for (BeregningsgrunnlagPrStatusOgAndel andel : beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList()) {
            BigDecimal bortfaltPrÅr = andel.getBgAndelArbeidsforhold().map(BGAndelArbeidsforhold::getNaturalytelseBortfaltPrÅr).orElse(null);
            BigDecimal tilkommetPrÅr = andel.getBgAndelArbeidsforhold().map(BGAndelArbeidsforhold::getNaturalytelseTilkommetPrÅr).orElse(null);
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

package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.datamapper.brev.FritekstmalBrevMapper.Brevdata;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerSvp;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class SvpMapper {

    private SvpMapper() {
    }

    public static int finnAntallRefusjonerTilArbeidsgivere(BeregningsresultatFP beregningsresultatFP) {
        return (int) beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .filter(BeregningsresultatAndel::erArbeidsgiverMottaker)
                .map(beregningsresultatAndel -> beregningsresultatAndel.getArbeidsgiver().map(Arbeidsgiver::getIdentifikator).orElse(beregningsresultatAndel.getArbeidsforholdRef().getReferanse()))
                .distinct().count();
    }

    public static SvpUttaksresultat utvidOgTilpassBrev(SvpUttaksresultat svpUttaksresultat, BeregningsresultatFP beregningsresultatFP
                                                       //List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder,
    ) {
        SvpUttaksresultat.Builder builder = SvpUttaksresultat.ny();

        List<SvpUttakResultatPeriode> uttakPerioder = svpUttaksresultat.getUttakResultatArbeidsforhold().stream()
                .flatMap(ur -> ur.getPerioder().stream()).collect(Collectors.toList());
        List<BeregningsresultatPeriode> beregningsresultatPerioder = beregningsresultatFP.getBeregningsresultatPerioder();

        builder.medAvslåttePerioder(plukkAvslåttePeridoder(uttakPerioder));

        Map<String, Set<SvpUttakResultatPeriode>> perioderPerArbeidsgiver = new HashMap<>();

        beregningsresultatPerioder.stream()
                .forEach(beregningsresultatPeriode -> {
                    //var matchetBgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
                    var matchetUttaksperiode = PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttakPerioder);
                    beregningsresultatPeriode.getBeregningsresultatAndelList().stream()  // map arbeidsforhold/aktiviteter
                            .forEach(andel -> {
                                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                String arbeidsgiverNavn = andel.getArbeidsgiver().map(Arbeidsgiver::getNavn)
                                        .orElse(aktivitetStatus.erFrilanser() ? "Som frilanser" : aktivitetStatus.erSelvstendigNæringsdrivende() ?
                                                "Som næringsdrivende" : matchetUttaksperiode.getArbeidsgiverNavn());
                                SvpUttakResultatPeriode periode = SvpUttakResultatPeriode.ny()
                                        .medAktivitetDagsats((long) andel.getDagsats())
                                        .medUtbetalingsgrad(matchetUttaksperiode.getUtbetalingsgrad())
                                        .medTidsperiode(matchetUttaksperiode.getTidsperiode())
                                        .build();
                                Set<SvpUttakResultatPeriode> periodeSet = perioderPerArbeidsgiver.getOrDefault(arbeidsgiverNavn, new HashSet<>());
                                periodeSet.add(periode);
                                perioderPerArbeidsgiver.put(arbeidsgiverNavn, periodeSet);
                            });
                });

        perioderPerArbeidsgiver.keySet().stream()
                .forEach(arbeidsgiverNavn -> {
                    SvpUttakResultatPerioder forArbeidsgiver = SvpUttakResultatPerioder.ny()
                            .medArbeidsgiverNavn(arbeidsgiverNavn)
                            .medPerioder(PeriodeMergerSvp.mergeLikePerioder(perioderPerArbeidsgiver.get(arbeidsgiverNavn).stream().sorted().collect(Collectors.toList())))
                            .build();
                    builder.medUttakResultatPerioder(forArbeidsgiver);
                });

        return builder.build();
    }

    private static List<SvpUttakResultatPeriode> plukkAvslåttePeridoder(List<SvpUttakResultatPeriode> uttakPerioder) {
        return uttakPerioder.stream()
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(Predicate.not(periode -> PeriodeIkkeOppfyltÅrsak.INGEN.equals(periode.getPeriodeIkkeOppfyltÅrsak())))
                .collect(Collectors.toList());
    }

    public static class SvpBrevData extends Brevdata {
        private SvpUttaksresultat uttakResultat;
        private BeregningsresultatFP beregningsresultatFP;
        private boolean refusjonTilBruker;
        private int refusjonerTilArbeidsgivere;

        public SvpBrevData(DokumentFelles dokumentFelles, FellesType fellesType,
                           SvpUttaksresultat uttakResultat, boolean refusjonTilBruker, int refusjonerTilArbeidsgivere) {
            super(dokumentFelles, fellesType);
            this.uttakResultat = uttakResultat;
            this.refusjonTilBruker = refusjonTilBruker;
            this.refusjonerTilArbeidsgivere = refusjonerTilArbeidsgivere;
        }

        public SvpUttaksresultat getResultat() {
            return uttakResultat;
        }

        public Map<DatoIntervall, Integer> getPeriodeDagsats() {
            Map<DatoIntervall, Integer> periodeDagsats = new TreeMap<>();
            uttakResultat.getUttakPerioder().stream()
                    .flatMap(s -> s.getPerioder().stream())
                    .forEach(p -> {
                        periodeDagsats.merge(p.getTidsperiode(),
                                (int) p.getAktivitetDagsats(),
                                (sats1, sats2) -> sats1 + sats2);
                    });
            return periodeDagsats;
        }

        public int getAntallPerioder() {
            return (int) uttakResultat.getUttakPerioder().stream()
                    .flatMap(arbeidsforhold -> arbeidsforhold.getPerioder().stream())
                    .count();
        }

        public int getAntallAvslag() {
            return uttakResultat.getAvslagPerioder().size();
        }

        public boolean isRefusjonTilBruker() {
            return refusjonTilBruker;
        }

        public int getRefusjonerTilArbeidsgivere() {
            return refusjonerTilArbeidsgivere;
        }

    }

}

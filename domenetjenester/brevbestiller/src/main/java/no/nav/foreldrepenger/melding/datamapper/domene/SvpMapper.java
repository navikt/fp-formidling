package no.nav.foreldrepenger.melding.datamapper.domene;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getArbeidsgiverNavn;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getBgBruttoPrÅr;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getMånedsinntekt;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
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

    public static Map<String, Object> mapFra(Beregningsgrunnlag beregningsgrunnlag, BeregningsresultatFP beregningsresultat, Behandling behandling) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Map> bgMap = mapFra(beregningsgrunnlag);

        // TODO Tor Map ferdig og erstatt hardkodede felter.
        map.put("nyEllerEndretBeregning", erNyEllerEndretBeregning(behandling));
        map.put("arbeidstaker", bgMap.get("arbeidstaker"));
        map.put("arbeidsforhold", mapFra(beregningsgrunnlag));
        map.put("ikkeSoktForAlleArbeidsforhold", false);
        map.put("frilanser",bgMap.get("frilanser"));
        map.put("ikkeSoktForAlleArbeidsforholdOgOppdrag", true);
        map.put("selvstendigNaringsdrivende", bgMap.get("selvstendigNaringsdrivende"));
        map.put("ikkeSoktForAlleArbeidsforholdOgNaringsvirksomhet", true);
        map.put("ikkeSoktForAlleOppdragOgNaringsvirksomhet", true);
        map.put("ikkeSoktForAlleArbeidsforholdOppdragOgNaringsvirksomhet", true);
        map.put("naturalYtelse", Map.of());
        map.put("militarSivil", false);
        map.put("fritekst", "");
        map.put("inntektOver6G", false);
        map.put("seksG", "<seksG>");
        map.put("lovhjemmel", "§ 8-2");

        return map;
    }

    private static Map<String, Map> mapFra(Beregningsgrunnlag beregningsgrunnlag) {
        Map<String, Map> map = new HashMap<>();
        map.put("arbeidstaker", new HashMap<>());
        map.put("frilanser", new HashMap<>());
        map.put("selvstendigNaringsdrivende", new HashMap<>());

        var bgpsaList = beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList();
        beregningsgrunnlag.getAktivitetStatuser()
                .forEach(bgAktivitetStatus ->
                        finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaList).stream()
                                .filter(andel -> getBgBruttoPrÅr(andel) != null)
                                .forEach(andel -> {
                                    BigDecimal bgBruttoPrÅr = getBgBruttoPrÅr(andel);
                                    AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                    if (aktivitetStatus.erArbeidstaker()) {
                                        leggTilArbeidsforhold(map, andel);
                                    }
                                    if (aktivitetStatus.erFrilanser()) {
                                        leggTilFrilansinntekt(map, andel);
                                    }
                                    if (aktivitetStatus.erSelvstendigNæringsdrivende()) {
                                        leggTilSnAndel(map, andel);
                                    }
                                }));
        return map;
    }

    private static void leggTilSnAndel(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel) {
        int sisteLignedeÅr = getSisteLignedeÅr(andel);
        map.merge("selvstendigNaringsdrivende",
                Map.of( "aarsinntekt", andel.getBruttoPrÅr().intValue(),
                        "nyoppstartet", TRUE.equals(andel.getNyIArbeidslivet()),
                        "sistLignedeAar1", sisteLignedeÅr,
                        "sistLignedeAar2", sisteLignedeÅr - 1,
                        "sistLignedeAar3", sisteLignedeÅr - 2),
                (map1, map2) -> {
                    map1.merge("aarsinntekt", map2.get("aarsinntekt"), adder());
                    map1.merge("nyoppstartet", map.get("nyoppstartet"), logicalOr());
                    return map1;
                });
    }

    private static BiFunction logicalOr() {
        return (bool, bool2) -> Boolean.logicalOr((boolean) bool, (boolean) bool2);
    }

    private static BiFunction adder() {
        return (i, i2) -> (int) i + (int) i2;
    }

    private static int getSisteLignedeÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBeregningsperiodeTom() == null ? 0 : andel.getBeregningsperiodeTom().getYear();
    }

    private static void leggTilFrilansinntekt(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel) {
        map.get("frilanser").merge("manedsinntekt", getMånedsinntekt(andel).intValue(), adder());
    }

    private static void leggTilArbeidsforhold(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel) {
        getArbeidsgiverNavn(andel).ifPresent(arbeidsgiverNavn ->
                ((HashSet) map.get("arbeidstaker").getOrDefault("arbeidsforhold", new HashSet<>())).add(Map.of(
                        "arbeidsgiverNavn", arbeidsgiverNavn, "manedsinntekt", getMånedsinntekt(andel).toString()
                        )
                ));
    }

    private static boolean erNyEllerEndretBeregning(Behandling behandling) {
        return behandling.erFørstegangssøknad() ||
                behandling.getBehandlingsresultat().getKonsekvenserForYtelsen().contains(ENDRING_I_BEREGNING);
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

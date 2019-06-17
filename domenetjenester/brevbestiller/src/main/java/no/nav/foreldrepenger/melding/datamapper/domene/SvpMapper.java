package no.nav.foreldrepenger.melding.datamapper.domene;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getArbeidsgiverNavn;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getBgBruttoPrÅr;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getMånedsinntekt;
import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemlerForBeregning;

import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerSvp;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.typer.Dato;
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

    public static Map<String, Object> mapFra(SvpUttaksresultat svpUttaksresultat, DokumentHendelse hendelse, Beregningsgrunnlag beregningsgrunnlag, BeregningsresultatFP beregningsresultat, Behandling behandling) {
        Map<String, Object> map = new HashMap<>();

        map.putAll(mapAtFlSnForholdFra(beregningsgrunnlag));
        mapIkkeSøkteAktiviteter(map);
        map.put("nyEllerEndretBeregning", erNyEllerEndretBeregning(behandling));
        map.put("naturalytelse", mapAktiviteter(svpUttaksresultat, beregningsresultat, beregningsgrunnlag).getOrDefault("naturalytelse", null));
        map.put("militarSivil", BeregningsgrunnlagMapper.militærEllerSivilTjeneste(beregningsgrunnlag));
        map.put("fritekst", BehandlingMapper.avklarFritekst(hendelse, behandling));
        map.put("inntektOver6G", BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
        map.put("seksG", BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).intValue());
        map.put("lovhjemmel", getLovhjemmelForBeregning(beregningsgrunnlag, behandling));

        return map;
    }

    private static void mapIkkeSøkteAktiviteter(Map<String, Object> map) {// TODO
        map.put("ikkeSoktForAlleArbeidsforhold", false);
        map.put("ikkeSoktForAlleArbeidsforholdOgOppdrag", false);
        map.put("ikkeSoktForAlleArbeidsforholdOgNaringsvirksomhet", false);
        map.put("ikkeSoktForAlleOppdragOgNaringsvirksomhet", false);
        map.put("ikkeSoktForAlleArbeidsforholdOppdragOgNaringsvirksomhet", false);
    }

    private static String getLovhjemmelForBeregning(Beregningsgrunnlag beregningsgrunnlag, Behandling behandling) {
        boolean revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        boolean innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        String konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        return formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForYtelsen, innvilget && revurdering);
    }

    private static Map<String, Map> mapAtFlSnForholdFra(Beregningsgrunnlag beregningsgrunnlag) {
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
                                    AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                    if (aktivitetStatus.erArbeidstaker()) {
                                        leggTilArbeidsforhold(map, andel);
                                    }
                                    if (aktivitetStatus.erFrilanser()) {
                                        leggTilFrilansinntekt(map, andel);
                                    }
                                    if (aktivitetStatus.erSelvstendigNæringsdrivende()) {
                                        leggTilSnAndel(map, andel, bgAktivitetStatus);
                                    }
                                }));
        return map;
    }

    private static void leggTilSnAndel(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel, BeregningsgrunnlagAktivitetStatus bgAktivitetStatus) {
        int sisteLignedeÅr = getSisteLignedeÅr(andel);
        map.merge("selvstendigNaringsdrivende",
                Map.of("aarsinntekt", andel.getBruttoPrÅr().intValue(),
                        "nyoppstartet", TRUE.equals(andel.getNyIArbeidslivet()),
                        "sistLignedeAar1", sisteLignedeÅr,
                        "sistLignedeAar2", sisteLignedeÅr - 1,
                        "sistLignedeAar3", sisteLignedeÅr - 2,
                        "inntekt_lavere_FL_SN", AktivitetStatus.KOMBINERT_FL_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel),
                        "inntekt_lavere_AT_FL_SN", AktivitetStatus.KOMBINERT_AT_FL_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel),
                        "inntekt_lavere_AT_SN", AktivitetStatus.KOMBINERT_AT_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel)),
                (map1, map2) -> {
                    map1.merge("aarsinntekt", map2.get("aarsinntekt"), adder());
                    map1.merge("nyoppstartet", map2.get("nyoppstartet"), logicalAnd());
                    return map1;
                });
    }

    private static boolean dagsatsErNull(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getDagsats() == null || andel.getDagsats() == 0;
    }

    private static BiFunction logicalAnd() {
        return (bool, bool2) -> Boolean.logicalAnd((boolean) bool, (boolean) bool2);
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

    public static boolean erNyEllerEndretBeregning(Behandling behandling) {
        return behandling.erFørstegangssøknad() ||
                behandling.getBehandlingsresultat().getKonsekvenserForYtelsen().contains(ENDRING_I_BEREGNING);
    }

    public static Map mapAktiviteter(SvpUttaksresultat svpUttaksresultat, BeregningsresultatFP beregningsresultatFP,
                                     Beregningsgrunnlag beregningsgrunnlag) {
        Map<String, Object> map = new HashMap<>();
        Set<Map> naturalytelser = new TreeSet<>(Comparator.comparing(naturalytelse -> ((ChronoLocalDate) naturalytelse.get("endringsDato"))));

        List<SvpUttakResultatPeriode> uttakPerioder = svpUttaksresultat.getUttakResultatArbeidsforhold().stream()
                .flatMap(ur -> ur.getPerioder().stream()).collect(Collectors.toList());
        List<BeregningsresultatPeriode> beregningsresultatPerioder = beregningsresultatFP.getBeregningsresultatPerioder();
        List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder = beregningsgrunnlag.getBeregningsgrunnlagPerioder();

        map.put("avslagPerioder", plukkAvslåttePeridoder(uttakPerioder));
        map.put("innvilgedePerioder", new HashSet<>());


        beregningsresultatPerioder.stream()
                .forEach(beregningsresultatPeriode -> {
                    var matchetBgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
                    var matchetUttaksperiode = PeriodeBeregner.finnUttakPeriode(beregningsresultatPeriode, uttakPerioder);
                    beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                            .forEach(andel -> {
                                // map arbeidsforhold/aktiviteter
                                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                String arbeidsgiverNavn = andel.getArbeidsgiver().map(Arbeidsgiver::getNavn)
                                        .orElse(aktivitetStatus.erFrilanser() ? "Som frilanser" : aktivitetStatus.erSelvstendigNæringsdrivende() ?
                                                "Som næringsdrivende" : matchetUttaksperiode.getArbeidsgiverNavn());
                                SvpUttakResultatPeriode periode = SvpUttakResultatPeriode.ny()
                                        .medAktivitetDagsats((long) andel.getDagsats())
                                        .medUtbetalingsgrad(matchetUttaksperiode.getUtbetalingsgrad())
                                        .medTidsperiode(matchetUttaksperiode.getTidsperiode())
                                        .build();
                                ((Collection) map.get("innvilgedePerioder")).add(Map.of("arbeidsgiverNavn", arbeidsgiverNavn, "perioder", periode));

                                // map naturalytelse
                                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(matchetBgPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)
                                        .ifPresent(bgAndel -> {
                                            bgAndel.getBgAndelArbeidsforhold().ifPresent(bgAndelArbeidsforhold -> {
                                                if (bgAndelArbeidsforhold.getNaturalytelseBortfaltPrÅr() != null ||
                                                        bgAndelArbeidsforhold.getNaturalytelseTilkommetPrÅr() != null) {
                                                    Map naturalytelse = mapNaturalytelse(matchetBgPeriode, beregningsresultatPeriode, arbeidsgiverNavn);
                                                    naturalytelser.add(naturalytelse);
                                                }
                                            });
                                        });
                            });
                });
        map.put("naturalytelse", naturalytelser);
        return map;
    }

    @Deprecated // TODO (Tor) Konsolideres i metoden over
    public static SvpUttaksresultat utvidOgTilpassBrev(SvpUttaksresultat svpUttaksresultat, BeregningsresultatFP beregningsresultatFP) {
        SvpUttaksresultat.Builder builder = SvpUttaksresultat.ny();

        List<SvpUttakResultatPeriode> uttakPerioder = svpUttaksresultat.getUttakResultatArbeidsforhold().stream()
                .flatMap(ur -> ur.getPerioder().stream()).collect(Collectors.toList());
        List<BeregningsresultatPeriode> beregningsresultatPerioder = beregningsresultatFP.getBeregningsresultatPerioder();
        builder.medAvslåttePerioder(plukkAvslåttePeridoder(uttakPerioder));

        Map<String, Set<SvpUttakResultatPeriode>> perioderPerArbeidsgiver = new HashMap<>();

        beregningsresultatPerioder.stream()
                .forEach(beregningsresultatPeriode -> {
                    //var matchetBgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
                    var matchetUttaksperiode = PeriodeBeregner.finnUttakPeriode(beregningsresultatPeriode, uttakPerioder);
                    beregningsresultatPeriode.getBeregningsresultatAndelList().stream()  // map arbeidsforhold/aktiviteter
                            .forEach(andel -> {
                                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                String arbeidsgiverNavn = andel.getArbeidsgiver().map(Arbeidsgiver::getNavn)
                                        .orElse(aktivitetStatus.erFrilanser() ? "Som frilanser" : aktivitetStatus.erSelvstendigNæringsdrivende() ?
                                                "Som næringsdrivende" : matchetUttaksperiode.getArbeidsgiverNavn());
                                SvpUttakResultatPeriode periode = SvpUttakResultatPeriode.ny()
                                        .medAktivitetDagsats((long) andel.getDagsats())
                                        .medUtbetalingsgrad(matchetUttaksperiode.getUtbetalingsgrad())
                                        .medTidsperiode(beregningsresultatPeriode.getPeriode())
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

    private static Map mapNaturalytelse(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode, BeregningsresultatPeriode beregningsresultatPeriode, String arbeidsgiverNavn) {
        Map<String, Object> map = new HashMap<>();
        for (PeriodeÅrsak årsak : beregningsgrunnlagPeriode.getperiodeÅrsaker()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.equals(årsak)) {
                map.put("bortfaller", true);
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                map.put("tilkommer", true);
            } else {
                continue;
            }
            map.put("arbeidsgiverNavn", arbeidsgiverNavn);
            map.put("nyDagsats", beregningsgrunnlagPeriode.getDagsats());
            map.put("endringsDato", Dato.medFormatering(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        }
        return map;
    }

    private static List<SvpUttakResultatPeriode> plukkAvslåttePeridoder(List<SvpUttakResultatPeriode> uttakPerioder) {
        return uttakPerioder.stream()
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(Predicate.not(periode -> PeriodeIkkeOppfyltÅrsak.INGEN.equals(periode.getPeriodeIkkeOppfyltÅrsak())))
                .collect(Collectors.toList());
    }


    public static int getAntallPerioder(SvpUttaksresultat uttakResultat) {
        return (int) uttakResultat.getUttakPerioder().stream()
                .flatMap(arbeidsforhold -> arbeidsforhold.getPerioder().stream())
                .count();
    }

    public static Map<DatoIntervall, Integer> getPeriodeDagsats(BeregningsresultatFP beregningsresultatFP) {
        Map<DatoIntervall, Integer> periodeDagsats = new TreeMap<>(DatoIntervall::compareTo);
        beregningsresultatFP.getBeregningsresultatPerioder().stream().forEach(p -> {
            periodeDagsats.put(p.getPeriode(), p.getDagsats().intValue());
        });
        return periodeDagsats;
    }
}

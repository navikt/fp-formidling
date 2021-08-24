package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

import java.time.chrono.ChronoLocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getMånedsinntekt;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerSvp.erPerioderSammenhengendeOgSkalSlåSammen;

@SuppressWarnings({ "unchecked", "java:S1192" })
public class SvpMapper {

    private SvpMapper() {
    }

    public static int finnAntallRefusjonerTilArbeidsgivere(BeregningsresultatFP beregningsresultatFP) {
        return (int) beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .filter(BeregningsresultatAndel::erArbeidsgiverMottaker)
                .map(beregningsresultatAndel -> beregningsresultatAndel.getArbeidsgiver().map(Arbeidsgiver::arbeidsgiverReferanse)
                        .orElse(beregningsresultatAndel.getArbeidsforholdRef() != null ? beregningsresultatAndel.getArbeidsforholdRef().getReferanse()
                                : "ukjent")) // om ikke annet som en sikring i test-miljøer.
                .distinct().count();
    }

    public static Map<String, Object> mapFra(SvpUttaksresultat svpUttaksresultat, DokumentHendelse hendelse, Beregningsgrunnlag beregningsgrunnlag,
            BeregningsresultatFP beregningsresultat, Behandling behandling) {
        Map<String, Object> map = new HashMap<>(mapAtFlSnForholdFra(beregningsgrunnlag));
        map.putAll(mapAktiviteter(svpUttaksresultat, beregningsresultat, beregningsgrunnlag));
        map.put("nyEllerEndretBeregning", erNyEllerEndretBeregning(behandling));
        map.put("bruttoBeregningsgrunnlag", BeregningsgrunnlagMapper.getAvkortetPrAarSVP(beregningsgrunnlag));
        map.put("militarSivil", BeregningsgrunnlagMapper.militærEllerSivilTjeneste(beregningsgrunnlag));
        map.put("inntektOver6G", BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
        map.put("seksG", BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).intValue());
        map.put("lovhjemmel", SvpUtledHjemmelForBeregning.utled(beregningsgrunnlag, behandling));
        mapIkkeSøkteAktiviteter(map);
        BehandlingMapper.avklarFritekst(hendelse, behandling).ifPresent(fritekst -> map.put("fritekst", fritekst));
        return map;
    }

    private static void mapIkkeSøkteAktiviteter(Map<String, Object> map) {// TODO Dette var data som ikke ble persistert av løsningen. Må evt. et
                                                                          // større tiltak til for å ta i bruk disse som parametre til å styre inn
                                                                          // tekst.
        map.put("ikkeSoktForAlleArbeidsforhold", false);
        map.put("ikkeSoktForAlleArbeidsforholdOgOppdrag", false);
        map.put("ikkeSoktForAlleArbeidsforholdOgNaringsvirksomhet", false);
        map.put("ikkeSoktForAlleOppdragOgNaringsvirksomhet", false);
        map.put("ikkeSoktForAlleArbeidsforholdOppdragOgNaringsvirksomhet", false);
    }

    private static Map<String, Map> mapAtFlSnForholdFra(Beregningsgrunnlag beregningsgrunnlag) {
        Map<String, Map> map = new HashMap<>();

        var bgpsaList = beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList();
        beregningsgrunnlag.getAktivitetStatuser()
                .forEach(bgAktivitetStatus -> BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaList).stream()
                        .filter(andel -> BeregningsgrunnlagMapper.getBgBruttoPrÅr(andel) != null)
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

    private static void leggTilSnAndel(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel,
            BeregningsgrunnlagAktivitetStatus bgAktivitetStatus) {
        int sisteLignedeÅr = getSisteLignedeÅr(andel);
        map.merge("selvstendigNaringsdrivende",
                new HashMap<>(Map.of("aarsinntekt", andel.getBruttoPrÅr().intValue(),
                        "nyoppstartet", TRUE.equals(andel.getNyIArbeidslivet()),
                        "sistLignedeAar1", String.valueOf(sisteLignedeÅr),
                        "sistLignedeAar2", String.valueOf(sisteLignedeÅr - 1),
                        "sistLignedeAar3", String.valueOf(sisteLignedeÅr - 2),
                        "inntekt_lavere_FL_SN", AktivitetStatus.KOMBINERT_FL_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel),
                        "inntekt_lavere_AT_FL_SN", AktivitetStatus.KOMBINERT_AT_FL_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel),
                        "inntekt_lavere_AT_SN", AktivitetStatus.KOMBINERT_AT_SN.equals(bgAktivitetStatus) && dagsatsErNull(andel))),
                (andel1, andel2) -> {
                    andel1.merge("aarsinntekt", andel2.get("aarsinntekt"), adder());
                    andel1.merge("nyoppstartet", andel2.get("nyoppstartet"), logicalAnd());
                    return andel1;
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
        int månedsinntektAndel = BeregningsgrunnlagMapper.getMånedsinntekt(andel).intValue();
        Map andeler = map.putIfAbsent("frilanser", new HashMap<>(Map.of("manedsinntekt", månedsinntektAndel)));
        if (andeler != null) {
            andeler.merge("manedsinntekt", månedsinntektAndel, adder());
        }
    }

    private static void leggTilArbeidsforhold(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel) {
        BeregningsgrunnlagMapper.getArbeidsgiverNavn(andel).ifPresent(arbeidsgiverNavn -> hentEllerOpprettNyArbeidsforholdListe(map).add(Map.of(
                "arbeidsgiverNavn", arbeidsgiverNavn, "manedsinntekt", getMånedsinntekt(andel).intValue())));
    }

    private static Set hentEllerOpprettNyArbeidsforholdListe(Map<String, Map> map) {
        map.putIfAbsent("arbeidstaker", new HashMap<>(Map.of("arbeidsforhold", new TreeSet<Map>(sotertEtterMånedsinntektHøyesteFørst()))));
        return (Set) map.get("arbeidstaker").get("arbeidsforhold");
    }

    private static Comparator<Object> sotertEtterMånedsinntektHøyesteFørst() {
        return Comparator.comparing(arbeidsforhold -> ((Integer) ((Map) arbeidsforhold).get("manedsinntekt"))).reversed();
    }

    private static boolean erNyEllerEndretBeregning(Behandling behandling) {
        return behandling.erFørstegangssøknad() ||
                behandling.getBehandlingsresultat().getKonsekvenserForYtelsen().contains(ENDRING_I_BEREGNING);
    }

    private static Map mapAktiviteter(SvpUttaksresultat svpUttaksresultat, BeregningsresultatFP beregningsresultatFP,
            Beregningsgrunnlag beregningsgrunnlag) {

        Map<String, Object> map = new HashMap<>();
        Set<Map> naturalytelser = new TreeSet<>(Comparator.comparing(naturalytelse -> ((ChronoLocalDate) naturalytelse.get("endringsDato"))));

        List<BeregningsresultatPeriode> beregningsresultatPerioder = beregningsresultatFP.getBeregningsresultatPerioder();
        List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder = beregningsgrunnlag.getBeregningsgrunnlagPerioder();

        List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold = svpUttaksresultat.getUttakResultatArbeidsforhold();
        List<SvpUttakResultatPeriode> uttakPerioder = uttakResultatArbeidsforhold.stream()
                .flatMap(ur -> ur.getPerioder().stream())
                .collect(Collectors.toList());

        map.put("avslagPerioder", SvpUtledAvslagPerioder.utled(uttakResultatArbeidsforhold));
        map.put("avslagArbeidsforhold", SvpUtledAvslagArbeidsforhold.utled(uttakResultatArbeidsforhold));
        map.put("uttakPerioder", new HashMap<>());

        LinkedList<SVPUtbetalingsperiodeInnvilgelse> periodeDagsats = new LinkedList<>();

        beregningsresultatPerioder.stream()
                .forEach(beregningsresultatPeriode -> {
                    var matchetBgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
                    var uttakPeriodeKandidater = PeriodeBeregner.finnUttakPeriodeKandidater(beregningsresultatPeriode, uttakPerioder);
                    beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                            .forEach(andel -> {
                                // map arbeidsforhold/aktiviteter
                                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                String arbeidsgiverNavn = utledArbeidsgiverNavn(andel, aktivitetStatus);

                                Optional<SvpUttakResultatPeriode> matchetUttaksperiode = finnUttakPeriode(uttakPeriodeKandidater, arbeidsgiverNavn);
                                if (!matchetUttaksperiode.isPresent()) {
                                    return;
                                }

                                mapAktivitet(map, matchetUttaksperiode.get(), beregningsresultatPeriode, andel, arbeidsgiverNavn);

                                if (harNaturalytelse(matchetBgPeriode, andel)) {
                                    Map naturalytelse = mapNaturalytelse(matchetBgPeriode, beregningsresultatPeriode, arbeidsgiverNavn);
                                    naturalytelser.add(naturalytelse);
                                }

                            });
                    if (beregningsresultatPeriode.getDagsats() > 0) { // FIXME Legg i egen loop
                        mapPeriodeDagsats(periodeDagsats, beregningsresultatPeriode);
                    }
                });

        map.put("antallPerioder", getAntallPerioder((Map) map.get("uttakPerioder")));
        map.put("periodeDagsats", periodeDagsats);
        map.put("stonadsperiodeTom", periodeDagsats.getLast().periode().getTom());
        map.put("naturalytelse", naturalytelser);

        return map;

    }

    private static String utledArbeidsgiverNavn(BeregningsresultatAndel andel, AktivitetStatus aktivitetStatus) {
        Optional<Arbeidsgiver> arbeidsgiverOpt = andel.getArbeidsgiver();
        if (arbeidsgiverOpt.isPresent()) {
            return arbeidsgiverOpt.get().navn();
        }
        if (aktivitetStatus.erFrilanser()) {
            return "Som frilanser";
        }
        if (aktivitetStatus.erSelvstendigNæringsdrivende()) {
            return "Som næringsdrivende";
        }
        return "";
    }

    private static void mapAktivitet(Map<String, Object> map, SvpUttakResultatPeriode matchetUttaksperiode,
            BeregningsresultatPeriode beregningsresultatPeriode, BeregningsresultatAndel andel,
            String arbeidsgiverNavn) {
        SvpUttakResultatPeriode uttakResultatPeriode = SvpUttakResultatPeriode.Builder.ny()
                .medAktivitetDagsats(andel.getDagsats())
                .medUtbetalingsgrad(matchetUttaksperiode.getUtbetalingsgrad())
                .medTidsperiode(beregningsresultatPeriode.getPeriode())
                .build();
        Map uttakPerioder = (Map) map.get("uttakPerioder");
        HashMap<String, TreeSet<SvpUttakResultatPeriode>> nyttPerioderMap = new HashMap<>(
                Map.of("perioder", new TreeSet<>(Set.of(uttakResultatPeriode))));
        Map eksisterendePerioderMap = (Map) uttakPerioder.putIfAbsent(arbeidsgiverNavn, nyttPerioderMap);
        if (eksisterendePerioderMap != null) {
            eksisterendePerioderMap.merge("perioder", uttakResultatPeriode, leggTilEllerMergeHvisSammenhengende());
        }
    }

    private static void mapPeriodeDagsats(LinkedList<SVPUtbetalingsperiodeInnvilgelse> periodeDagsats, BeregningsresultatPeriode beregningsresultatPeriode) {
        if (periodeDagsats.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(periodeDagsats.getLast(), beregningsresultatPeriode)) {
            periodeDagsats.add(new SVPUtbetalingsperiodeInnvilgelse(beregningsresultatPeriode));
        } else {
            var sammenhengendeFom = periodeDagsats.pollLast().periode().getFom();
            periodeDagsats.add(new SVPUtbetalingsperiodeInnvilgelse(
                    beregningsresultatPeriode.getDagsats(),
                    beregningsresultatPeriode.getUtbetaltTilSoker(),
                    DatoIntervall.fraOgMedTilOgMed(sammenhengendeFom, beregningsresultatPeriode.getPeriode().getTom())));
        }
    }

    private static Optional<SvpUttakResultatPeriode> finnUttakPeriode(List<SvpUttakResultatPeriode> matchendeUttaksperioder,
            String arbeidsgiverNavn) {
        return matchendeUttaksperioder.stream().filter(uttakPeriode -> !uttakPeriode.getArbeidsgiverNavn().isEmpty())
                .filter(uttakPeriode -> arbeidsgiverNavn.contains(uttakPeriode.getArbeidsgiverNavn()))
                .findFirst();
    }

    private static int getAntallPerioder(Map uttakPerioder) {
        return uttakPerioder.values().stream()
                .mapToInt(foreachArbeidsforhold -> ((Set) ((Map) foreachArbeidsforhold).get("perioder")).size())
                .sum();
    }

    private static BiFunction leggTilEllerMergeHvisSammenhengende() {
        return (perioder, nyPeriode) -> {
            ((Set) perioder).add(nyPeriode);
            var perioderEtterSammenslåing = new TreeSet<SvpUttakResultatPeriode>();
            return ((Set) perioder).stream()
                    .reduce(perioderEtterSammenslåing, slåSammenSammenhengendePerioder(), dummyCombiner());
        };
    }

    private static BinaryOperator dummyCombiner() { // trengs ikke for sekvensielle streams
        return (result1, result2) -> {
            ((Set) result1).addAll((Set) result2);
            return result1;
        };
    }

    private static BiFunction slåSammenSammenhengendePerioder() {
        return (delresultat, element) -> {
            var next = (SvpUttakResultatPeriode) element;
            var resultat = (TreeSet<SvpUttakResultatPeriode>) delresultat;
            if (resultat.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(resultat.last(), next)) {
                resultat.add(next);
            } else {
                var previous = resultat.pollLast();
                resultat.add(SvpUttakResultatPeriode.Builder.ny(previous)
                        .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(previous.getFom(), next.getTom()))
                        .build());
            }
            return resultat;
        };
    }

    private static boolean harNaturalytelse(BeregningsgrunnlagPeriode matchetBgPeriode, BeregningsresultatAndel andel) {
        return PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(matchetBgPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)
                .flatMap(BeregningsgrunnlagPrStatusOgAndel::getBgAndelArbeidsforhold)
                .filter(bgAndelArbeidsforhold -> bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null ||
                        bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null)
                .isPresent();
    }

    private static Map mapNaturalytelse(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode, BeregningsresultatPeriode beregningsresultatPeriode,
            String arbeidsgiverNavn) {
        Map<String, Object> map = new HashMap<>();
        String endringType = null;
        for (String årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode().equals(årsak)) {
                endringType = "bortfaller";
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.getKode().equals(årsak)) {
                endringType = "tilkommer";
            } else {
                endringType = null;
            }
        }
        if (endringType != null) {
            map.put("arbeidsgiverNavn", arbeidsgiverNavn);
            map.put(endringType, true);
            map.put("nyDagsats", beregningsgrunnlagPeriode.getDagsats());
            map.put("endringsDato", Dato.medFormatering(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        }
        return map;
    }

}

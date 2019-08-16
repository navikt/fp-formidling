package no.nav.foreldrepenger.melding.datamapper.domene;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getArbeidsgiverNavn;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getBgBruttoPrÅr;
import static no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper.getMånedsinntekt;
import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemlerForBeregning;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerSvp.erPerioderSammenhengendeOgSkalSlåSammen;

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
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

@SuppressWarnings("unchecked")
public class SvpMapper {

    private SvpMapper() {
    }

    public static int finnAntallRefusjonerTilArbeidsgivere(BeregningsresultatFP beregningsresultatFP) {
        return (int) beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .filter(BeregningsresultatAndel::erArbeidsgiverMottaker)
                .map(beregningsresultatAndel -> beregningsresultatAndel.getArbeidsgiver().map(Arbeidsgiver::getIdentifikator)
                        .orElse(beregningsresultatAndel.getArbeidsforholdRef() != null ?
                                beregningsresultatAndel.getArbeidsforholdRef().getReferanse() : "ukjent")) // om ikke annet som en sikring i test-miljøer.
                .distinct().count();
    }

    public static Map<String, Object> mapFra(SvpUttaksresultat svpUttaksresultat, DokumentHendelse hendelse, Beregningsgrunnlag beregningsgrunnlag, BeregningsresultatFP beregningsresultat, Behandling behandling) {
        Map<String, Object> map = new HashMap<>(mapAtFlSnForholdFra(beregningsgrunnlag));
        map.putAll(mapAktiviteter(svpUttaksresultat, beregningsresultat, beregningsgrunnlag));
        map.put("nyEllerEndretBeregning", erNyEllerEndretBeregning(behandling));
        map.put("bruttoBeregningsgrunnlag", BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        map.put("militarSivil", BeregningsgrunnlagMapper.militærEllerSivilTjeneste(beregningsgrunnlag));
        map.put("inntektOver6G", BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
        map.put("seksG", BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).intValue());
        map.put("lovhjemmel", getLovhjemmelForBeregning(beregningsgrunnlag, behandling));
        mapIkkeSøkteAktiviteter(map);
        BehandlingMapper.avklarFritekst(hendelse, behandling).ifPresent(fritekst -> map.put("fritekst", fritekst));
        return map;
    }

    private static void mapIkkeSøkteAktiviteter(Map<String, Object> map) {// TODO Dette var data som ikke ble persistert av løsningen. Må evt. et større tiltak til for å ta i bruk disse som parametre til å styre inn tekst.
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
        int månedsinntektAndel = getMånedsinntekt(andel).intValue();
        Map andeler = map.putIfAbsent("frilanser", new HashMap<>(Map.of("manedsinntekt", månedsinntektAndel)));
        if (andeler != null) {
            andeler.merge("manedsinntekt", månedsinntektAndel, adder());
        }
    }

    private static void leggTilArbeidsforhold(Map<String, Map> map, BeregningsgrunnlagPrStatusOgAndel andel) {
        getArbeidsgiverNavn(andel).ifPresent(arbeidsgiverNavn -> hentEllerOpprettNyArbeidsforholdListe(map).add(Map.of(
                "arbeidsgiverNavn", arbeidsgiverNavn, "manedsinntekt", getMånedsinntekt(andel).intValue()
        )));
    }

    private static Set hentEllerOpprettNyArbeidsforholdListe(Map<String, Map> map) {
        map.putIfAbsent("arbeidstaker", new HashMap<>(Map.of("arbeidsforhold", new TreeSet<Map>(sotertEtterMånedsinntektHøyesteFørst()))));
        return (Set) map.get("arbeidstaker").get("arbeidsforhold");
    }

    private static Comparator<Object> sotertEtterMånedsinntektHøyesteFørst() {
        return Comparator.comparing(arbeidsforhold -> ((Integer) ((Map) arbeidsforhold).get("manedsinntekt"))).reversed();
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
        map.put("uttakPerioder", new HashMap<>());

        LinkedList<BeregningsresultatPeriode> periodeDagsats = new LinkedList<>();

        beregningsresultatPerioder.stream()
                .forEach(beregningsresultatPeriode -> {
                    var matchetBgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder);
                    var uttakPeriodeKandidater = PeriodeBeregner.finnUttakPeriodeKandidater(beregningsresultatPeriode, uttakPerioder);
                    beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                            .forEach(andel -> {
                                // map arbeidsforhold/aktiviteter
                                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                                String arbeidsgiverNavn = andel.getArbeidsgiver().map(Arbeidsgiver::getNavn)
                                        .orElse(aktivitetStatus.erFrilanser() ? "Som frilanser" : aktivitetStatus.erSelvstendigNæringsdrivende() ?
                                                "Som næringsdrivende" : "");

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
                    if (beregningsresultatPeriode.getDagsats() > 0) {
                        mapPeriodeDagsats(periodeDagsats, beregningsresultatPeriode);
                    }
                });

        map.put("antallPerioder", getAntallPerioder((Map) map.get("uttakPerioder")));
        map.put("periodeDagsats", periodeDagsats);
        map.put("stonadsperiodeTom", periodeDagsats.getLast().getPeriode().getTom());
        map.put("naturalytelse", naturalytelser);

        return map;

    }

    private static void mapAktivitet(Map<String, Object> map,
                                     SvpUttakResultatPeriode matchetUttaksperiode, BeregningsresultatPeriode beregningsresultatPeriode,
                                     BeregningsresultatAndel andel,
                                     String arbeidsgiverNavn) {
        SvpUttakResultatPeriode uttakResultat = SvpUttakResultatPeriode.ny()
                .medAktivitetDagsats((long) andel.getDagsats())
                .medUtbetalingsgrad(matchetUttaksperiode.getUtbetalingsgrad())
                .medTidsperiode(beregningsresultatPeriode.getPeriode())
                .build();

        Map eksisterendeMap = (Map) ((Map) map.get("uttakPerioder"))
                .putIfAbsent(arbeidsgiverNavn, new HashMap<>(Map.of("perioder", new TreeSet<>(Set.of(uttakResultat)))));
        if (eksisterendeMap != null) {
            eksisterendeMap.merge("perioder", uttakResultat, leggTilEllerMergeHvisSammenhengende());
        }
    }

    private static void mapPeriodeDagsats(LinkedList<BeregningsresultatPeriode> periodeDagsats, BeregningsresultatPeriode beregningsresultatPeriode) {
        if (periodeDagsats.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(periodeDagsats.getLast(), beregningsresultatPeriode)) {
            periodeDagsats.add(beregningsresultatPeriode);
        } else {
            var sammenhengendeFom = periodeDagsats.pollLast().getPeriode().getFom();
            periodeDagsats.add(BeregningsresultatPeriode.ny()
                    .medPeriode(DatoIntervall.fraOgMedTilOgMed(sammenhengendeFom, beregningsresultatPeriode.getPeriode().getTom()))
                    .medDagsats(beregningsresultatPeriode.getDagsats())
                    .build());
        }
    }

    private static Optional<SvpUttakResultatPeriode> finnUttakPeriode(List<SvpUttakResultatPeriode> matchendeUttaksperioder, String arbeidsgiverNavn) {
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
                resultat.add(SvpUttakResultatPeriode.ny(previous)
                        .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(previous.getFom(), next.getTom()))
                        .build());
            }
            return resultat;
        };
    }

    private static boolean harNaturalytelse(BeregningsgrunnlagPeriode matchetBgPeriode, BeregningsresultatAndel andel) {
        return PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(matchetBgPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)
                .flatMap(BeregningsgrunnlagPrStatusOgAndel::getBgAndelArbeidsforhold)
                .filter(bgAndelArbeidsforhold -> bgAndelArbeidsforhold.getNaturalytelseBortfaltPrÅr() != null ||
                        bgAndelArbeidsforhold.getNaturalytelseTilkommetPrÅr() != null)
                .isPresent();
    }

    private static Map mapNaturalytelse(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode, BeregningsresultatPeriode beregningsresultatPeriode, String arbeidsgiverNavn) {
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

    public static List<Map> plukkAvslåttePeridoder(List<SvpUttakResultatPeriode> uttakPerioder) {
        return uttakPerioder.stream()
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(Predicate.not(periode -> PeriodeIkkeOppfyltÅrsak.INGEN.equals(periode.getPeriodeIkkeOppfyltÅrsak())))
                .map(AvslåttPeriode -> Map.of(
                        "fom", AvslåttPeriode.getFom(), "tom", AvslåttPeriode.getTom(),
                        "aarsakskode", AvslåttPeriode.getAarsakskode(), "arbeidsgiverNavn", AvslåttPeriode.getArbeidsgiverNavn()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

}

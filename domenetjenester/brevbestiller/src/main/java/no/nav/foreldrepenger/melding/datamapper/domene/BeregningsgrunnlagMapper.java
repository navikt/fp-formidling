package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AndelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AndelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.StatusTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagMapper {
    public BeregningsgrunnlagMapper() {
        // CDI Proxy
    }

    public static BeregningsgrunnlagRegelListeType mapRegelListe(Beregningsgrunnlag beregningsgrunnlag) {
        ObjectFactory objectFactory = new ObjectFactory();
        BeregningsgrunnlagRegelListeType regelListe = objectFactory.createBeregningsgrunnlagRegelListeType();
        List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe = finnFørstePeriode(beregningsgrunnlag).getBeregningsgrunnlagPrStatusOgAndelList();
        for (BeregningsgrunnlagAktivitetStatus bgAktivitetStatus : beregningsgrunnlag.getAktivitetStatuser()) {
            BeregningsgrunnlagRegelType beregningsgrunnlagRegel = objectFactory.createBeregningsgrunnlagRegelType();
            List<BeregningsgrunnlagPrStatusOgAndel> filtrertListe = finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe);
            beregningsgrunnlagRegel.setRegelStatus(tilStatusTypeKode(bgAktivitetStatus.getAktivitetStatus()));
            beregningsgrunnlagRegel.setAndelListe(mapAndelListe(filtrertListe));
            beregningsgrunnlagRegel.setAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(tellAntallArbeidsforholdIBeregningUtenSluttpakke(filtrertListe));
            beregningsgrunnlagRegel.setBesteBeregning(harNoenAvAndeleneBesteberegning(filtrertListe));
            beregningsgrunnlagRegel.setSNNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
            regelListe.getBeregningsgrunnlagRegel().add(beregningsgrunnlagRegel);
        }
        return regelListe;
    }

    private static Map<AktivitetStatus, StatusTypeKode> aktivitetStatusKodeStatusTypeKodeMap = new HashMap<>();
    private static Map<AktivitetStatus, List<AktivitetStatus>> kombinerteRegelStatuserMap = new HashMap<>();

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN, List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    static {
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSTAKER, StatusTypeKode.ARBEIDSTAKER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.FRILANSER, StatusTypeKode.FRILANSER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, StatusTypeKode.SELVSTENDIG_NÆRINGSDRIVENDE);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL, StatusTypeKode.KOMBINERT_AT_FL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN, StatusTypeKode.KOMBINERT_AT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_SN, StatusTypeKode.KOMBINERT_AT_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_FL_SN, StatusTypeKode.KOMBINERT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.DAGPENGER, StatusTypeKode.DAGPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSAVKLARINGSPENGER, StatusTypeKode.ARBEIDSAVKLARINGSPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.MILITÆR_ELLER_SIVIL, StatusTypeKode.MILITÆR_ELLER_SIVIL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.BRUKERS_ANDEL, StatusTypeKode.BRUKERSANDEL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KUN_YTELSE, StatusTypeKode.KUN_YTELSE);
    }

    public static boolean erOverbetalt(Beregningsgrunnlag beregningsgrunnlag, Beregningsgrunnlag originaltBeregningsgrunnlag) {
        if (originaltBeregningsgrunnlag == null) {
            return false;
        }
        return finnFørstePeriode(beregningsgrunnlag).getDagsats() < finnFørstePeriode(originaltBeregningsgrunnlag).getDagsats();
    }

    public static boolean militærEllerSivilTjeneste(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getAktivitetStatuser().contains(AktivitetStatus.MILITÆR_ELLER_SIVIL);
    }

    public static long finnBrutto(Beregningsgrunnlag beregningsgrunnlag) {
        AtomicLong sum = new AtomicLong();
        finnFørstePeriode(beregningsgrunnlag)
                .getBeregningsgrunnlagPrStatusOgAndelList()
                .forEach(andel -> {
                    if (andel.getAvkortetPrÅr() != null) {
                        sum.addAndGet(andel.getAvkortetPrÅr().longValue());
                    } else if (andel.getBruttoPrÅr() != null) {
                        sum.addAndGet(andel.getBruttoPrÅr().longValue());
                    }
                });
        return sum.get();
    }

    static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus, List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagPrStatusOgAndel> resultatListe;
        if (AktivitetStatus.KOMBINERTE_STATUSER.contains(bgAktivitetStatus.getAktivitetStatus())) {
            List<AktivitetStatus> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.getAktivitetStatus());
            resultatListe = bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        } else {
            resultatListe = bgpsaListe.stream().filter(andel -> bgAktivitetStatus.getAktivitetStatus().equals(andel.getAktivitetStatus())).collect(Collectors.toList());
        }
        if (resultatListe.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(Kodeliste::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.getAktivitetStatus(), sb));
        }
        return resultatListe;
    }

    static AndelType lagAndelType(BeregningsgrunnlagPrStatusOgAndel andel) {
        ObjectFactory objectFactory = new ObjectFactory();
        AndelType andelType = objectFactory.createAndelType();
        andelType.setStatus(tilStatusTypeKode(andel.getAktivitetStatus()));
        andelType.setDagsats(andel.getOriginalDagsatsFraTilstøtendeYtelse() == null ? andel.getDagsats() : andel.getOriginalDagsatsFraTilstøtendeYtelse());
        andelType.setEtterlønnSluttpakke(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType()));
        BigDecimal bgBruttoPrÅr = getBgBruttoPrÅr(andel);
        if (bgBruttoPrÅr != null) {
            andelType.setMånedsinntekt(getMånedsinntekt(andel).longValue());
            andelType.setÅrsinntekt(andel.getBruttoPrÅr().longValue());
        }
        if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())) {
            andelType.setPensjonsgivendeInntekt(andel.getPgiSnitt() == null ? 0 : andel.getPgiSnitt().longValue());
            andelType.setSisteLignedeÅr(andel.getBeregningsperiodeTom() == null ? 0 : (long) andel.getBeregningsperiodeTom().getYear());
        }
        if (AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())) {
            getArbeidsgiverNavn(andel).ifPresent(andelType::setArbeidsgiverNavn);
        }

        return andelType;
    }

    static BigDecimal getMånedsinntekt(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::getNavn);
    }

    static BigDecimal getBgBruttoPrÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
    }

    static BigInteger tellAntallArbeidsforholdIBeregningUtenSluttpakke
            (List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return BigInteger.valueOf(bgpsaListe.stream()
                .filter(bgpsa -> AktivitetStatus.ARBEIDSTAKER.equals(bgpsa.getAktivitetStatus()))
                .filter(bgpsa -> !OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(bgpsa.getArbeidsforholdType()))
                .count());
    }

    static boolean harNoenAvAndeleneBesteberegning(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .anyMatch(andel -> andel.getBesteberegningPrÅr() != null);
    }

    static boolean nyoppstartetSelvstendingNæringsdrivende(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
                .filter(andel -> andel.getNyIArbeidslivet() != null)
                .map(BeregningsgrunnlagPrStatusOgAndel::getNyIArbeidslivet)
                .findFirst()
                .orElse(false);
    }

    static AndelListeType mapAndelListe(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        //Litt stygt å ha disse her, spesielt uten qualifiers
        ObjectFactory objectFactory = new ObjectFactory();
        AndelListeType andelListeType = objectFactory.createAndelListeType();
        bgpsaListe.forEach(bgpsa -> andelListeType.getAndel().add(lagAndelType(bgpsa)));
        return andelListeType;
    }

    private static BeregningsgrunnlagPeriode finnFørstePeriode(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);
    }

    public static BigDecimal finnSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getGrunnbeløp().multipliser(6).getVerdi();
    }

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    static StatusTypeKode tilStatusTypeKode(AktivitetStatus statuskode) {
        if (aktivitetStatusKodeStatusTypeKodeMap.containsKey(statuskode)) {
            return aktivitetStatusKodeStatusTypeKodeMap.get(statuskode);
        }
        throw new IllegalArgumentException("Utviklerfeil: Fant ikke riktig aktivitetstatus " + statuskode);
    }

    public static long getHalvGOrElseZero(Optional<Beregningsgrunnlag> beregningsgrunnlag) {
        return beregningsgrunnlag.map(Beregningsgrunnlag::getGrunnbeløp).map(Beløp::getVerdi).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN).longValue();
    }
}

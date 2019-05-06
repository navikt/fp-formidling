package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
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
            beregningsgrunnlagRegel.setAntallArbeidsgivereIBeregning(tellAntallArbeidsforholdIBeregning(filtrertListe));
            beregningsgrunnlagRegel.setBesteBeregning(harNoenAvAndeleneBesteberegning(filtrertListe));
            beregningsgrunnlagRegel.setSNNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
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
        if (AktivitetStatus.KOMBINERTE_STATUSER.contains(bgAktivitetStatus.getAktivitetStatus())) {
            List<AktivitetStatus> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.getAktivitetStatus());
            return bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        }
        return bgpsaListe.stream().filter(andel -> bgAktivitetStatus.getAktivitetStatus().equals(andel.getAktivitetStatus())).collect(Collectors.toList());
    }

    static AndelType lagAndelType(BeregningsgrunnlagPrStatusOgAndel andel) {
        ObjectFactory objectFactory = new ObjectFactory();
        AndelType andelType = objectFactory.createAndelType();
        andelType.setStatus(tilStatusTypeKode(andel.getAktivitetStatus()));
        andelType.setDagsats(andel.getOriginalDagsatsFraTilstøtendeYtelse() == null ? andel.getDagsats() : andel.getOriginalDagsatsFraTilstøtendeYtelse());
        BigDecimal bgBruttoPrÅr = andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
        if (bgBruttoPrÅr != null) {
            andelType.setMånedsinntekt(andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
            andelType.setÅrsinntekt(andel.getBruttoPrÅr().longValue());
        }
        if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())) {
            andelType.setPensjonsgivendeInntekt(andel.getPgiSnitt() == null ? null : andel.getPgiSnitt().longValue());
            andelType.setSisteLignedeÅr(andel.getBeregningsperiodeTom() == null ? null : (long) andel.getBeregningsperiodeTom().getYear());
        }
        if (AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())) {
            andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::getNavn).ifPresent(andelType::setArbeidsgiverNavn);
            if (OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType())) {
                andelType.setEtterlønnSluttpakke(true);
            }
        }

        return andelType;
    }

    static BigInteger tellAntallArbeidsforholdIBeregning(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return BigInteger.valueOf(bgpsaListe.stream()
                .filter(bgpsa -> AktivitetStatus.ARBEIDSTAKER.equals(bgpsa.getAktivitetStatus()))
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

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag, BigDecimal seksG) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(seksG) > 0;
    }

    static StatusTypeKode tilStatusTypeKode(AktivitetStatus statuskode) {
        if (aktivitetStatusKodeStatusTypeKodeMap.containsKey(statuskode)) {
            return aktivitetStatusKodeStatusTypeKodeMap.get(statuskode);
        }
        throw new IllegalArgumentException("Utviklerfeil: Fant ikke riktig aktivitetstatus " + statuskode);
    }


}

package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemlerForBeregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Frilanser;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class BeregningMapper {

    private static Map<AktivitetStatus, List<AktivitetStatus>> kombinerteRegelStatuserMap = new EnumMap<>(AktivitetStatus.class);

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL, List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN,
                List.of(AktivitetStatus.ARBEIDSTAKER, AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN,
                List.of(AktivitetStatus.FRILANSER, AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE));
    }

    public static List<Arbeidsforhold> mapArbeidsforhold(Beregningsgrunnlag beregningsgrunnlag) {
        List<Arbeidsforhold> resultat = new ArrayList<>();
        for (BeregningsgrunnlagPrStatusOgAndel andel : getAndeler(beregningsgrunnlag)) {
            if (andel.getAktivitetStatus().erArbeidstaker()) {
                getArbeidsgiverNavn(andel).ifPresent(navn -> resultat.add(Arbeidsforhold.ny()
                        .medArbeidsgiverNavn(navn)
                        .medMånedsinntekt(getMånedsinntekt(andel).longValue())
                        .build()));
            }
        }
        resultat.sort(Comparator.comparing(Arbeidsforhold::getMånedsinntekt).reversed()); // Høyeste månedsinntekt først
        return resultat;
    }

    public static SelvstendigNæringsdrivende mapSelvstendigNæringsdrivende(Beregningsgrunnlag beregningsgrunnlag) {
        SelvstendigNæringsdrivende resultat = null;
        for (BeregningsgrunnlagPrStatusOgAndel andel : getAndeler(beregningsgrunnlag)) {
            if (andel.getAktivitetStatus().erSelvstendigNæringsdrivende()) {
                resultat = SelvstendigNæringsdrivende.ny(resultat)
                        .medNyoppstartet(TRUE.equals(andel.getNyIArbeidslivet()))
                        .medÅrsinntekt(andel.getBruttoPrÅr().longValue())
                        .medSistLignedeÅr(getSisteLignedeÅr(andel))
                        .medInntektLavere_AT_SN(AktivitetStatus.KOMBINERT_AT_SN.equals(andel.getAktivitetStatus()) && dagsatsErNull(andel))
                        .medInntektLavere_AT_FL_SN(AktivitetStatus.KOMBINERT_AT_FL_SN.equals(andel.getAktivitetStatus()) && dagsatsErNull(andel))
                        .medInntektLavere_FL_SN(AktivitetStatus.KOMBINERT_FL_SN.equals(andel.getAktivitetStatus()) && dagsatsErNull(andel))
                        .build();
            }
        }
        return resultat;
    }

    public static Frilanser mapFrilanser(Beregningsgrunnlag beregningsgrunnlag) {
        Frilanser resultat = null;
        for (BeregningsgrunnlagPrStatusOgAndel andel : getAndeler(beregningsgrunnlag)) {
            if (andel.getAktivitetStatus().erFrilanser()) {
                resultat = Frilanser.ny(resultat)
                        .leggTilMånedsinntekt(getMånedsinntekt(andel).longValue())
                        .build();
            }
        }
        return resultat;
    }

    // Spesielt for SVP: avkortetPerÅr gir faktisk beregningsgrunnlag for alle arbeidsforhold, og ikke bare arbeidsforholdet det søkes om
    public static BigDecimal getAvkortetPrÅrSVP(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getAvkortetPrÅr();
    }

    public static boolean erMilitærSivil(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getAktivitetStatuser().stream()
                .map(BeregningsgrunnlagAktivitetStatus::aktivitetStatus)
                .collect(Collectors.toList())
                .contains(AktivitetStatus.MILITÆR_ELLER_SIVIL);
    }

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    public static BigDecimal finnSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getGrunnbeløp().multipliser(6).getVerdi();
    }

    public static String utledLovhjemmelForBeregning(Beregningsgrunnlag beregningsgrunnlag, Behandling behandling) {
        boolean revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        boolean innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        String konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());

        // Workaround for å unngå manuelt arbeid for saksbehandlere - Se TFP-1032
        // Fpsak sender hjemmel 14-7, men SVP skal referere til hjemmel 14-4. Fpsak burde egentlig sende 14-4 for SVP,
        // men hjemmelen blir utledet kun for FP (dvs. 14-7) i dag. Denne hacken erstatter 14-7 med 14-4 for SVP før
        // brevet blir generert. Dette er en workaround inntil hjemmel for flere ytelsestyper (SVP, ES, FP, etc.) blir
        // implementert i Fpsak.
        String hjemmel = beregningsgrunnlag.getHjemmel().getNavn();
        if (hjemmel != null) {
            hjemmel = hjemmel.replace("14-7", "14-4");
        }

        return formaterLovhjemlerForBeregning(hjemmel, konsekvensForYtelsen, innvilget && revurdering, behandling);
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> getAndeler(Beregningsgrunnlag beregningsgrunnlag) {
        List<BeregningsgrunnlagPrStatusOgAndel> bgpsaList = beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList();
        List<BeregningsgrunnlagPrStatusOgAndel> andeler = new ArrayList<>();
        beregningsgrunnlag.getAktivitetStatuser()
                .forEach(bgAktivitetStatus -> andeler.addAll(finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaList).stream()
                .filter(andel -> BeregningsgrunnlagMapper.getBgBruttoPrÅr(andel) != null)
                .collect(Collectors.toList())));
        return andeler;
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus,
                                                                                          List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        List<BeregningsgrunnlagPrStatusOgAndel> resultatListe;
        if (AktivitetStatus.KUN_YTELSE.equals(bgAktivitetStatus.aktivitetStatus())) {
            return bgpsaListe;
        }
        if (bgAktivitetStatus.aktivitetStatus().harKombinertStatus()) {
            List<AktivitetStatus> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.aktivitetStatus());
            resultatListe = bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        } else {
            resultatListe = bgpsaListe.stream().filter(andel -> bgAktivitetStatus.aktivitetStatus().equals(andel.getAktivitetStatus()))
                    .collect(Collectors.toList());
        }
        if (resultatListe.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            bgpsaListe.stream().map(BeregningsgrunnlagPrStatusOgAndel::getAktivitetStatus).map(AktivitetStatus::getKode).forEach(sb::append);
            throw new IllegalStateException(String.format("Fant ingen andeler for status: %s, andeler: %s", bgAktivitetStatus.aktivitetStatus(), sb));
        }
        return resultatListe;
    }

    private static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::navn);
    }

    private static BigDecimal getMånedsinntekt(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private static int getSisteLignedeÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getBeregningsperiodeTom() == null ? 0 : andel.getBeregningsperiodeTom().getYear();
    }

    private static boolean dagsatsErNull(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getDagsats() == null || andel.getDagsats() == 0;
    }

    private static BeregningsgrunnlagPeriode finnFørstePeriode(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);
    }
}

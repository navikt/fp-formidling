package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnBgpsaListe;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnFørstePeriode;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper.formaterLovhjemlerForBeregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Frilanser;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public final class BeregningMapper {

    public static List<Arbeidsforhold> mapArbeidsforhold(Beregningsgrunnlag beregningsgrunnlag) {
        List<Arbeidsforhold> resultat = new ArrayList<>();
        for (var andel : getAndeler(beregningsgrunnlag)) {
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
        for (var andel : getAndeler(beregningsgrunnlag)) {
            if (andel.getAktivitetStatus().erSelvstendigNæringsdrivende()) {
                resultat = SelvstendigNæringsdrivende.ny(resultat)
                        .medNyoppstartet(TRUE.equals(andel.getNyIArbeidslivet()))
                        .leggTilÅrsinntekt(andel.getBruttoPrÅr())
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
        for (var andel : getAndeler(beregningsgrunnlag)) {
            if (andel.getAktivitetStatus().erFrilanser()) {
                resultat = Frilanser.ny(resultat)
                        .leggTilMånedsinntekt(getMånedsinntekt(andel))
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
        return harMilitærStatusMedDagsatsOgAnnenStatus(finnBgpsaListe(beregningsgrunnlag));
    }

    private static boolean harMilitærStatusMedDagsatsOgAnnenStatus(List<BeregningsgrunnlagPrStatusOgAndel> andeler) {
        return andeler.stream()
                .filter(status -> AktivitetStatus.MILITÆR_ELLER_SIVIL.equals(status.getAktivitetStatus()))
                .anyMatch(andel -> andel.getDagsats() > 0);
    }

    public static boolean inntektOverSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).getBruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    public static BigDecimal finnSeksG(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getGrunnbeløp().multipliser(6).getVerdi();
    }

    public static String utledLovhjemmelForBeregning(Beregningsgrunnlag beregningsgrunnlag, Behandling behandling) {
        var revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        var innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        var konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());

        // Workaround for å unngå manuelt arbeid for saksbehandlere - Se TFP-1032
        // Fpsak sender hjemmel 14-7, men SVP skal referere til hjemmel 14-4. Fpsak burde egentlig sende 14-4 for SVP,
        // men hjemmelen blir utledet kun for FP (dvs. 14-7) i dag. Denne hacken erstatter 14-7 med 14-4 for SVP før
        // brevet blir generert. Dette er en workaround inntil hjemmel for flere ytelsestyper (SVP, ES, FP, etc.) blir
        // implementert i Fpsak.
        var hjemmel = beregningsgrunnlag.getHjemmel().getNavn();
        if (hjemmel != null) {
            hjemmel = hjemmel.replace("14-7", "14-4");
        }

        return formaterLovhjemlerForBeregning(hjemmel, konsekvensForYtelsen, innvilget && revurdering, behandling);
    }

    private static List<BeregningsgrunnlagPrStatusOgAndel> getAndeler(Beregningsgrunnlag beregningsgrunnlag) {
        var bgpsaList = beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList();
        List<BeregningsgrunnlagPrStatusOgAndel> andeler = new ArrayList<>();
        beregningsgrunnlag.getAktivitetStatuser()
                .forEach(bgAktivitetStatus -> andeler.addAll(finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaList).stream()
                        .filter(andel -> getBgBruttoPrÅr(andel) != null)
                        .toList()));
        return andeler;
    }

    private static BigDecimal getBgBruttoPrÅr(BeregningsgrunnlagPrStatusOgAndel andel) {
        return andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
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

}

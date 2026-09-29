package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.lang.Boolean.TRUE;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnAktivitetStatuserForAndeler;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper.finnFørstePeriode;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper.formaterLovhjemlerForBeregning;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.ARBEIDSTAKER;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.FRILANSER;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.KOMBINERT_AT_FL;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.KOMBINERT_AT_FL_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.KOMBINERT_AT_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.KOMBINERT_FL_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Frilanser;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;

public final class BeregningMapper {

    private static final Set<AktivitetStatusDto> AT_STATUSER = Set.of(ARBEIDSTAKER, KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_AT_FL);
    private static final Set<AktivitetStatusDto> SN_STATUSER = Set.of(SELVSTENDIG_NÆRINGSDRIVENDE, KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_FL_SN);
    private static final Set<AktivitetStatusDto> FL_STATUSER = Set.of(FRILANSER, KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL, KOMBINERT_FL_SN);

    private BeregningMapper() {
    }

    public static List<Arbeidsforhold> mapArbeidsforhold(BeregningsgrunnlagDto beregningsgrunnlag, UnaryOperator<String> hentNavn) {
        List<Arbeidsforhold> resultat = new ArrayList<>();
        for (var andel : getAndeler(beregningsgrunnlag)) {
            if (AT_STATUSER.contains(andel.aktivitetStatus())) {
                getArbeidsgiverNavn(andel, hentNavn).ifPresent(navn -> resultat.add(
                    Arbeidsforhold.ny().medArbeidsgiverNavn(navn).medMånedsinntekt(getMånedsinntekt(andel).longValue()).build()));
            }
        }
        resultat.sort(Comparator.comparing(Arbeidsforhold::getMånedsinntekt).reversed()); // Høyeste månedsinntekt først
        return resultat;
    }

    public static SelvstendigNæringsdrivende mapSelvstendigNæringsdrivende(BeregningsgrunnlagDto beregningsgrunnlag) {
        SelvstendigNæringsdrivende resultat = null;
        for (var andel : getAndeler(beregningsgrunnlag)) {
            if (SN_STATUSER.contains(andel.aktivitetStatus())) {
                resultat = SelvstendigNæringsdrivende.ny(resultat)
                    .medNyoppstartet(TRUE.equals(andel.erNyIArbeidslivet()))
                    .leggTilÅrsinntekt(andel.bruttoPrÅr())
                    .medSistLignedeÅr(getSisteLignedeÅr(andel))
                    .medInntektLavereAtSn(AktivitetStatusDto.KOMBINERT_AT_SN.equals(andel.aktivitetStatus()) && dagsatsErNull(andel))
                    .medInntektLavereAtFlSn(AktivitetStatusDto.KOMBINERT_AT_FL_SN.equals(andel.aktivitetStatus()) && dagsatsErNull(andel))
                    .medInntektLavereFlSn(AktivitetStatusDto.KOMBINERT_FL_SN.equals(andel.aktivitetStatus()) && dagsatsErNull(andel))
                    .build();
            }
        }
        return resultat;
    }

    public static Frilanser mapFrilanser(BeregningsgrunnlagDto beregningsgrunnlag) {
        Frilanser resultat = null;
        for (var andel : getAndeler(beregningsgrunnlag)) {
            if (FL_STATUSER.contains(andel.aktivitetStatus())) {
                resultat = Frilanser.ny(resultat).leggTilMånedsinntekt(getMånedsinntekt(andel)).build();
            }
        }
        return resultat;
    }

    public static BigDecimal getAvkortetPrÅrSVP(BeregningsgrunnlagDto beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).bruttoPrÅr();
    }

    public static boolean erMilitærSivil(BeregningsgrunnlagDto beregningsgrunnlag) {
        return harMilitærStatusMedDagsatsOgAnnenStatus(finnFørstePeriode(beregningsgrunnlag).beregningsgrunnlagandeler());
    }

    private static boolean harMilitærStatusMedDagsatsOgAnnenStatus(List<BeregningsgrunnlagAndelDto> andeler) {
        return andeler.stream()
            .filter(status -> AktivitetStatusDto.MILITÆR_ELLER_SIVIL.equals(status.aktivitetStatus()))
            .anyMatch(andel -> andel.dagsats() > 0);
    }

    public static boolean inntektOverSeksG(BeregningsgrunnlagDto beregningsgrunnlag) {
        return finnFørstePeriode(beregningsgrunnlag).bruttoPrÅr().compareTo(finnSeksG(beregningsgrunnlag)) > 0;
    }

    public static BigDecimal finnSeksG(BeregningsgrunnlagDto beregningsgrunnlag) {
        return beregningsgrunnlag.grunnbeløp().multiply(BigDecimal.valueOf(6));
    }

    public static String utledLovhjemmelForBeregning(BeregningsgrunnlagDto beregningsgrunnlag, BrevGrunnlagDto behandling) {
        var revurdering = behandling.erRevurdering();
        var behandlingsresultat = behandling.behandlingsresultat();
        var innvilget = BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.INNVILGET.equals(behandlingsresultat.behandlingResultatType());
        var konsekvensForYtelsen = BehandlingMapper.kodeFra(behandlingsresultat.konsekvenserForYtelsen());

        // Workaround for å unngå manuelt arbeid for saksbehandlere - Se TFP-1032
        // Fpsak sender hjemmel 14-7, men SVP skal referere til hjemmel 14-4. Fpsak burde egentlig sende 14-4 for SVP,
        // men hjemmelen blir utledet kun for FP (dvs. 14-7) i dag. Denne hacken erstatter 14-7 med 14-4 for SVP før
        // brevet blir generert. Dette er en workaround inntil hjemmel for flere ytelsestyper (SVP, ES, FP, etc.) blir
        // implementert i Fpsak.
        var hjemmel = KodeverkMapper.mapBeregningHjemmel(beregningsgrunnlag.hjemmel()).getLovRef();
        if (hjemmel != null) {
            hjemmel = hjemmel.replace("14-7", "14-4");
        }

        return formaterLovhjemlerForBeregning(hjemmel, konsekvensForYtelsen, innvilget && revurdering, behandling.uuid());
    }

    private static List<BeregningsgrunnlagAndelDto> getAndeler(BeregningsgrunnlagDto beregningsgrunnlag) {
        var bgpsaList = beregningsgrunnlag.beregningsgrunnlagperioder().getFirst().beregningsgrunnlagandeler();
        List<BeregningsgrunnlagAndelDto> andeler = new ArrayList<>();
        beregningsgrunnlag.aktivitetstatusListe()
            .forEach(bgAktivitetStatus -> andeler.addAll(
                finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaList).stream().filter(andel -> getBgBruttoPrÅr(andel) != null).toList()));
        return andeler;
    }

    private static BigDecimal getBgBruttoPrÅr(BeregningsgrunnlagAndelDto andel) {
        return andel.avkortetPrÅr() != null ? andel.avkortetPrÅr() : andel.bruttoPrÅr();
    }

    private static Optional<String> getArbeidsgiverNavn(BeregningsgrunnlagAndelDto andel, UnaryOperator<String> hentNavn) {
        return Optional.ofNullable(andel.arbeidsforhold()).map(a -> hentNavn.apply(a.arbeidsgiverIdent()));
    }

    private static BigDecimal getMånedsinntekt(BeregningsgrunnlagAndelDto andel) {
        return andel.bruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private static int getSisteLignedeÅr(BeregningsgrunnlagAndelDto andel) {
        return andel.beregningsperiodeTom() == null ? 0 : andel.beregningsperiodeTom().getYear();
    }

    private static boolean dagsatsErNull(BeregningsgrunnlagAndelDto andel) {
        return andel.dagsats() == null || andel.dagsats() == 0;
    }

}

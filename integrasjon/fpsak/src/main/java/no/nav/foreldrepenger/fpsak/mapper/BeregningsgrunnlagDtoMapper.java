package no.nav.foreldrepenger.fpsak.mapper;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpsak.dto.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDtoV2;
import no.nav.foreldrepenger.fpsak.dto.beregningsgrunnlag.v2.BeregningsgrunnlagDtoV2;
import no.nav.foreldrepenger.fpsak.dto.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDtoV2;
import no.nav.foreldrepenger.fpsak.dto.beregningsgrunnlag.v2.BgAndelArbeidsforholdDtoV2;
import no.nav.foreldrepenger.fpsak.mapper.sortering.PeriodeComparator;

public class BeregningsgrunnlagDtoMapper {

    private static DatoIntervall avklarBeregningsperiode(BeregningsgrunnlagAndelDtoV2 dto) {
        if (dto.getBeregningsperiodeTom() == null) {
            if (dto.getBeregningsperiodeFom() == null) {
                return null;
            }
            return DatoIntervall.fraOgMed(dto.getBeregningsperiodeFom());
        }
        return DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsperiodeFom(), dto.getBeregningsperiodeTom());
    }

    private static Arbeidsgiver mapArbeidsgiverFraDto(BgAndelArbeidsforholdDtoV2 dto, UnaryOperator<String> hentNavn) {
        return new Arbeidsgiver(dto.getArbeidsgiverIdent(), hentNavn.apply(dto.getArbeidsgiverIdent()));
    }

    public static Beregningsgrunnlag mapBeregningsgrunnlagFraDto(BeregningsgrunnlagDtoV2 dto, UnaryOperator<String> hentNavn) {
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny();
        builder.medGrunnbeløp(new Beløp(dto.getGrunnbeløp()));
        dto.getAktivitetstatusListe().stream().map(BeregningsgrunnlagDtoMapper::mapBeregningsgrunnlagAktivitetStatusFraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        dto.getBeregningsgrunnlagperioder().stream()
                .map(periode -> mapBeregningsgrunnlagPeriodeFraDto(periode, hentNavn))
                .sorted(PeriodeComparator.BEREGNINGSGRUNNLAG)
                .forEach(builder::leggTilBeregningsgrunnlagPeriode);
        builder.medhHjemmel(dto.getHjemmel());
        builder.medBesteberegnet(dto.isErBesteberegnet());
        return builder.build();
    }

    private static BeregningsgrunnlagPeriode mapBeregningsgrunnlagPeriodeFraDto(BeregningsgrunnlagPeriodeDtoV2 dto, UnaryOperator<String> hentNavn) {
        DatoIntervall intervall = dto.getBeregningsgrunnlagperiodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagperiodeFom(), dto.getBeregningsgrunnlagperiodeTom()) :
                DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagperiodeFom());
        return BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(dto.getBruttoPrÅr())
                .medAvkortetPrÅr(dto.getAvkortetPrÅr())
                .medDagsats(dto.getDagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(dto.getPeriodeårsaker())
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.getBeregningsgrunnlagandeler().stream()
                        .map(andel -> mapBgpsaFraDto(andel, hentNavn))
                        .collect(Collectors.toList()))
                .build();
    }

    private static BeregningsgrunnlagPrStatusOgAndel mapBgpsaFraDto(BeregningsgrunnlagAndelDtoV2 dto, UnaryOperator<String> hentNavn) {
        BeregningsgrunnlagPrStatusOgAndel.Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = null;
        if (dto.getArbeidsforhold() != null) {
            bgAndelArbeidsforhold = mapBgAndelArbeidsforholdfraDto(dto.getArbeidsforhold(), hentNavn);
        }
        builder.medAktivitetStatus(dto.getAktivitetStatus())
                .medBruttoPrÅr(dto.getBruttoPrÅr())
                .medAvkortetPrÅr(dto.getAvkortetPrÅr())
                .medNyIArbeidslivet(dto.getErNyIArbeidslivet())
                .medDagsats(dto.getDagsats())
                .medBeregningsperiode(avklarBeregningsperiode(dto))
                .medBgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .medArbeidsforholdType(bgAndelArbeidsforhold == null ? OpptjeningAktivitetType.UDEFINERT : dto.getArbeidsforholdType())
                .medErTilkommetAndel(dto.getErTilkommetAndel());
        return builder.build();
    }

    private static BGAndelArbeidsforhold mapBgAndelArbeidsforholdfraDto(BgAndelArbeidsforholdDtoV2 dto, UnaryOperator<String> hentNavn) {
        return new BGAndelArbeidsforhold(mapArbeidsgiverFraDto(dto, hentNavn),
                ArbeidsforholdRef.ref(dto.getArbeidsforholdRef()),
                dto.getNaturalytelseBortfaltPrÅr(),
                dto.getNaturalytelseTilkommetPrÅr()
        );
    }

    private static BeregningsgrunnlagAktivitetStatus mapBeregningsgrunnlagAktivitetStatusFraDto(AktivitetStatus aktivitetStatus) {
        return new BeregningsgrunnlagAktivitetStatus(aktivitetStatus);
    }
}

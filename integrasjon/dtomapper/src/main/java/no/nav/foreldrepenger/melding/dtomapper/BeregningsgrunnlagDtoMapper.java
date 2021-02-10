package no.nav.foreldrepenger.melding.dtomapper;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagArbeidsforholdDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelSNDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.dtomapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagDtoMapper {

    private static DatoIntervall avklarBeregningsperiode(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        if (dto.getBeregningsgrunnlagTom() == null) {
            if (dto.getBeregningsgrunnlagFom() == null) {
                return null;
            }
            return DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagFom());
        }
        return DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagFom(), dto.getBeregningsgrunnlagTom());
    }

    private static Arbeidsgiver mapArbeidsgiverFraDto(BeregningsgrunnlagArbeidsforholdDto dto, UnaryOperator<String> hentNavn) {
        return new Arbeidsgiver(dto.getArbeidsgiverIdent(), hentNavn.apply(dto.getArbeidsgiverIdent()));
    }

    public static Beregningsgrunnlag mapBeregningsgrunnlagFraDto(BeregningsgrunnlagDto dto, UnaryOperator<String> hentNavn) {
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny();
        builder.medGrunnbeløp(new Beløp(dto.getGrunnbeløp()));
        dto.getAktivitetStatus().stream().map(BeregningsgrunnlagDtoMapper::mapBeregningsgrunnlagAktivitetStatusFraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        dto.getBeregningsgrunnlagPeriode().stream()
                .map(periode -> mapBeregningsgrunnlagPeriodeFraDto(periode, hentNavn))
                .sorted(PeriodeComparator.BEREGNINGSGRUNNLAG)
                .forEach(builder::leggTilBeregningsgrunnlagPeriode);
        builder.medhHjemmel(Hjemmel.fraKode(dto.getHjemmel().getKode()));
        return builder.build();
    }

    private static BeregningsgrunnlagPeriode mapBeregningsgrunnlagPeriodeFraDto(BeregningsgrunnlagPeriodeDto dto, UnaryOperator<String> hentNavn) {
        DatoIntervall intervall = dto.getBeregningsgrunnlagPeriodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagPeriodeFom(), dto.getBeregningsgrunnlagPeriodeTom()) :
                DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagPeriodeFom());
        return BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medRedusertPrÅr(dto.getRedusertPrAar())
                .medAvkortetPrÅr(dto.getAvkortetPrAar())
                .medDagsats(dto.getDagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(dto.getPeriodeAarsaker().stream().map(KodeDto::getKode).collect(Collectors.toList()))
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.getBeregningsgrunnlagPrStatusOgAndel().stream()
                        .map(andel -> mapBgpsaFraDto(andel, hentNavn))
                        .collect(Collectors.toList()))
                .build();
    }

    private static BeregningsgrunnlagPrStatusOgAndel mapBgpsaFraDto(BeregningsgrunnlagPrStatusOgAndelDto dto, UnaryOperator<String> hentNavn) {
        BeregningsgrunnlagPrStatusOgAndel.Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = null;
        if (dto.getArbeidsforhold() != null) {
            bgAndelArbeidsforhold = mapBgAndelArbeidsforholdfraDto(dto.getArbeidsforhold(), hentNavn);
        }
        builder.medAktivitetStatus(AktivitetStatus.fraKode(dto.getAktivitetStatus().getKode()))
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medAvkortetPrÅr(dto.getAvkortetPrAar())
                .medBesteberegningPrÅr(dto.getBesteberegningPrAar())
                .medOverstyrtPrÅr(dto.getOverstyrtPrAar())
                .medNyIArbeidslivet(dto.getErNyIArbeidslivet())
                .medOriginalDagsatsFraTilstøtendeYtelse(dto.getOriginalDagsatsFraTilstøtendeYtelse())
                .medDagsats(dto.getDagsats())
                .medBeregningsperiode(avklarBeregningsperiode(dto))
                .medBgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .medArbeidsforholdType(bgAndelArbeidsforhold == null ? OpptjeningAktivitetType.UDEFINERT : bgAndelArbeidsforhold.getArbeidsforholdType());
        if (dto instanceof BeregningsgrunnlagPrStatusOgAndelSNDto) {
            BeregningsgrunnlagPrStatusOgAndelSNDto snDto = (BeregningsgrunnlagPrStatusOgAndelSNDto) dto;
            builder.medPgi1(snDto.getPgi1())
                    .medPgi2(snDto.getPgi2())
                    .medPgi3(snDto.getPgi3())
                    .medPgiSnitt(snDto.getPgiSnitt());
        }
        return builder.build();
    }

    private static BGAndelArbeidsforhold mapBgAndelArbeidsforholdfraDto(BeregningsgrunnlagArbeidsforholdDto dto, UnaryOperator<String> hentNavn) {
        return new BGAndelArbeidsforhold(mapArbeidsgiverFraDto(dto, hentNavn),
                ArbeidsforholdRef.ref(dto.getArbeidsforholdId()),
                OpptjeningAktivitetType.fraKode(dto.getArbeidsforholdType().getKode()),
                dto.getNaturalytelseBortfaltPrÅr(),
                dto.getNaturalytelseTilkommetPrÅr()
        );
    }

    private static BeregningsgrunnlagAktivitetStatus mapBeregningsgrunnlagAktivitetStatusFraDto(KodeDto kodedto) {
        return new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.fraKode(kodedto.getKode()));
    }
}

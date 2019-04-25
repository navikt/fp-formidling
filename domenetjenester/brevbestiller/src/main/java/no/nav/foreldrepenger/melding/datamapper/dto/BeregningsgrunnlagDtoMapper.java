package no.nav.foreldrepenger.melding.datamapper.dto;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelSNDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;

@ApplicationScoped
public class BeregningsgrunnlagDtoMapper {

    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;


    @Inject
    public BeregningsgrunnlagDtoMapper(KodeverkRepository kodeverkRepository,
                                       BehandlingRestKlient behandlingRestKlient) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public BeregningsgrunnlagDtoMapper() {
        //CDI
    }

    private static DatoIntervall avklarBeregningsperiode(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        if (dto.getBeregningsgrunnlagTom() == null) {
            return DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagFom());
        }
        return DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagFom(), dto.getBeregningsgrunnlagTom());
    }

    public Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        return mapBeregningsgrunnlagFraDto(behandlingRestKlient.hentBeregningsgrunnlag(behandling.getResourceLinkDtos()));
    }

    public Beregningsgrunnlag mapBeregningsgrunnlagFraDto(BeregningsgrunnlagDto dto) {
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny();
        //TODO - vi burde ikke trenge å gange
        builder.medGrunnbeløp(new Beløp(BigDecimal.valueOf(dto.getHalvG()).multiply(BigDecimal.valueOf(2))));
        dto.getAktivitetStatus().stream().map(this::mapBeregningsgrunnlagAktivitetStatusFraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        dto.getBeregningsgrunnlagPeriode().stream().map(this::mapBeregningsgrunnlagPeriodeFraDto).forEach(builder::leggTilBeregningsgrunnlagPeriode);
        return builder.build();
    }

    BeregningsgrunnlagPeriode mapBeregningsgrunnlagPeriodeFraDto(BeregningsgrunnlagPeriodeDto dto) {
        DatoIntervall intervall = dto.getBeregningsgrunnlagPeriodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagPeriodeFom(), dto.getBeregningsgrunnlagPeriodeTom()) :
                DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagPeriodeFom());
        return BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medRedusertPrÅr(dto.getRedusertPrAar())
                .medDagsats(dto.getDagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(dto.getPeriodeAarsaker().stream().map(KodeDto::getKode).collect(Collectors.toList()))
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.getBeregningsgrunnlagPrStatusOgAndel().stream().map(this::mapBgpsaFraDto).collect(Collectors.toList()))
                .build();
    }

    BeregningsgrunnlagPrStatusOgAndel mapBgpsaFraDto(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        BeregningsgrunnlagPrStatusOgAndel.Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = BGAndelArbeidsforhold.fraDto(dto.getArbeidsforhold());
        builder.medAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, dto.getAktivitetStatus().kode))
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medAvkortetPrÅr(dto.getAvkortetPrAar())
                .medBesteberegningPrÅr(dto.getBesteberegningPrAar())
                .medOverstyrtPrÅr(dto.getOverstyrtPrAar())
                .medNyIArbeidslivet(dto.getErNyIArbeidslivet())
                //TODO - mangler verdi
                .medOriginalDagsatsFraTilstøtendeYtelse(null)
                //TODO - mangler verdi
                .medDagsatsArbeidsgiver(null)
                //TODO - mangler verdi
                .medDagsatsBruker(null)
                .medBeregningsperiode(avklarBeregningsperiode(dto))
                .medBgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .medArbeidsforholdType(bgAndelArbeidsforhold.getArbeidsforholdType());
        if (dto instanceof BeregningsgrunnlagPrStatusOgAndelSNDto) {
            BeregningsgrunnlagPrStatusOgAndelSNDto snDto = (BeregningsgrunnlagPrStatusOgAndelSNDto) dto;
            builder.medPgi1(snDto.getPgi1())
                    .medPgi2(snDto.getPgi2())
                    .medPgi3(snDto.getPgi3())
                    .medPgiSnitt(snDto.getPgiSnitt());
        }
        return builder.build();
    }

    BeregningsgrunnlagAktivitetStatus mapBeregningsgrunnlagAktivitetStatusFraDto(KodeDto kodedto) {
        return new BeregningsgrunnlagAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, kodedto.kode));
    }
}

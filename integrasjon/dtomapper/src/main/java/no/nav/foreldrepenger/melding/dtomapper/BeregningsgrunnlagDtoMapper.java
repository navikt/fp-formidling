package no.nav.foreldrepenger.melding.dtomapper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.dtomapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

@ApplicationScoped
public class BeregningsgrunnlagDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public BeregningsgrunnlagDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
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

    private static Arbeidsgiver mapArbeidsgiverFraDto(BeregningsgrunnlagArbeidsforholdDto dto) {
        AktørId aktørId = null;
        Virksomhet virksomhet = null;
        if (dto.getAktørId() != null) {
            aktørId = new AktørId(dto.getAktørId());
        } else {
            virksomhet = new Virksomhet(dto.getArbeidsgiverNavn(), dto.getArbeidsgiverId());
        }
        return new Arbeidsgiver(dto.getArbeidsgiverNavn(), virksomhet, aktørId);
    }

    public Beregningsgrunnlag mapBeregningsgrunnlagFraDto(BeregningsgrunnlagDto dto) {
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny();
        builder.medGrunnbeløp(new Beløp(BigDecimal.valueOf(dto.getHalvG()).multiply(BigDecimal.valueOf(2))));
        dto.getAktivitetStatus().stream().map(this::mapBeregningsgrunnlagAktivitetStatusFraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        dto.getBeregningsgrunnlagPeriode().stream()
                .map(this::mapBeregningsgrunnlagPeriodeFraDto)
                .sorted(PeriodeComparator.BEREGNINGSGRUNNLAG)
                .forEach(builder::leggTilBeregningsgrunnlagPeriode);
        builder.medhHjemmel(kodeverkRepository.finn(Hjemmel.class, dto.getHjemmel().getKode()));
        return builder.build();
    }

    private BeregningsgrunnlagPeriode mapBeregningsgrunnlagPeriodeFraDto(BeregningsgrunnlagPeriodeDto dto) {
        DatoIntervall intervall = dto.getBeregningsgrunnlagPeriodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagPeriodeFom(), dto.getBeregningsgrunnlagPeriodeTom()) :
                DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagPeriodeFom());
        return BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medRedusertPrÅr(dto.getRedusertPrAar())
                .medDagsats(dto.getDagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(dto.getPeriodeAarsaker().stream().map(this::finnPeriodeÅrsakFraDto).collect(Collectors.toList()))
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.getBeregningsgrunnlagPrStatusOgAndel().stream().map(this::mapBgpsaFraDto).collect(Collectors.toList()))
                .build();
    }

    private PeriodeÅrsak finnPeriodeÅrsakFraDto(KodeDto dto) {
        return kodeverkRepository.finn(PeriodeÅrsak.class, dto.getKode());
    }

    private BeregningsgrunnlagPrStatusOgAndel mapBgpsaFraDto(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        BeregningsgrunnlagPrStatusOgAndel.Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = null;
        if (dto.getArbeidsforhold() != null) {
            bgAndelArbeidsforhold = mapBgAndelArbeidsforholdfraDto(dto.getArbeidsforhold());
        }
        builder.medAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, dto.getAktivitetStatus().getKode()))
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

    private BGAndelArbeidsforhold mapBgAndelArbeidsforholdfraDto(BeregningsgrunnlagArbeidsforholdDto dto) {
        return new BGAndelArbeidsforhold(mapArbeidsgiverFraDto(dto),
                ArbeidsforholdRef.ref(dto.getArbeidsforholdId()),
                kodeverkRepository.finn(OpptjeningAktivitetType.class, dto.getArbeidsforholdType().getKode()),
                dto.getNaturalytelseBortfaltPrÅr(),
                dto.getNaturalytelseTilkommetPrÅr()
        );
    }

    private BeregningsgrunnlagAktivitetStatus mapBeregningsgrunnlagAktivitetStatusFraDto(KodeDto kodedto) {
        return new BeregningsgrunnlagAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, kodedto.getKode()));
    }
}

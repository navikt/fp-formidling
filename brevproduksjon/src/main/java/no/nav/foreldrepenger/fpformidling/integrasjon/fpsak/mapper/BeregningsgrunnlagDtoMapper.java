package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.fpformidling.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;

public class BeregningsgrunnlagDtoMapper {

    public static Beregningsgrunnlag mapFraDto(BeregningsgrunnlagDto dto, UnaryOperator<String> hentNavn) {
        Beregningsgrunnlag.Builder builder = Beregningsgrunnlag.ny();
        builder.medGrunnbeløp(new Beløp(dto.grunnbeløp()));
        dto.aktivitetstatusListe().stream().map(BeregningsgrunnlagDtoMapper::mapBeregningsgrunnlagAktivitetStatusFraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        dto.beregningsgrunnlagperioder().stream()
                .map(periode -> mapBeregningsgrunnlagPeriodeFraDto(periode, hentNavn))
                .sorted(PeriodeComparator.BEREGNINGSGRUNNLAG)
                .forEach(builder::leggTilBeregningsgrunnlagPeriode);
        builder.medhHjemmel(mapHjemmelFraDto(dto.hjemmel()));
        builder.medBesteberegnet(dto.erBesteberegnet());
        return builder.build();
    }

    static Hjemmel mapHjemmelFraDto(HjemmelDto hjemmel) {
        if (hjemmel == null) return Hjemmel.UDEFINERT;
        return switch (hjemmel) {
            case F_14_7 -> Hjemmel.F_14_7;
            case F_14_7_8_28_8_30 -> Hjemmel.F_14_7_8_28_8_30;
            case F_14_7_8_30 -> Hjemmel.F_14_7_8_30;
            case F_14_7_8_35 -> Hjemmel.F_14_7_8_35;
            case F_14_7_8_38 -> Hjemmel.F_14_7_8_38;
            case F_14_7_8_40 -> Hjemmel.F_14_7_8_40;
            case F_14_7_8_41 -> Hjemmel.F_14_7_8_41;
            case F_14_7_8_42 -> Hjemmel.F_14_7_8_42;
            case F_14_7_8_43 -> Hjemmel.F_14_7_8_43;
            case F_14_7_8_47 -> Hjemmel.F_14_7_8_47;
            case F_14_7_8_49 -> Hjemmel.F_14_7_8_49;
        };
    }

    private static BeregningsgrunnlagPeriode mapBeregningsgrunnlagPeriodeFraDto(BeregningsgrunnlagPeriodeDto dto, UnaryOperator<String> hentNavn) {
        DatoIntervall intervall = dto.beregningsgrunnlagperiodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.beregningsgrunnlagperiodeFom(), dto.beregningsgrunnlagperiodeTom()) :
                DatoIntervall.fraOgMed(dto.beregningsgrunnlagperiodeFom());
        return BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(dto.bruttoPrÅr())
                .medAvkortetPrÅr(dto.avkortetPrÅr())
                .medDagsats(dto.dagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(mapPeriodeårsakerFraDto(dto.periodeårsaker()))
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.beregningsgrunnlagandeler().stream()
                        .map(andel -> mapBgpsaFraDto(andel, hentNavn))
                        .collect(Collectors.toList()))
                .build();
    }

    private static List<PeriodeÅrsak> mapPeriodeårsakerFraDto(List<PeriodeÅrsakDto> periodeårsaker) {
        return periodeårsaker.stream().map(BeregningsgrunnlagDtoMapper::mapPeriodeårsakFraDto).toList();
    }

    static PeriodeÅrsak mapPeriodeårsakFraDto(PeriodeÅrsakDto periodeÅrsak) {
        if (periodeÅrsak == null) return null;
        return switch (periodeÅrsak) {
            case ARBEIDSFORHOLD_AVSLUTTET -> PeriodeÅrsak.ARBEIDSFORHOLD_AVSLUTTET;
            case ENDRING_I_AKTIVITETER_SØKT_FOR -> PeriodeÅrsak.ENDRING_I_AKTIVITETER_SØKT_FOR;
            case ENDRING_I_REFUSJONSKRAV -> PeriodeÅrsak.ENDRING_I_REFUSJONSKRAV;
            case GRADERING -> PeriodeÅrsak.GRADERING;
            case GRADERING_OPPHØRER -> PeriodeÅrsak.GRADERING_OPPHØRER;
            case NATURALYTELSE_BORTFALT -> PeriodeÅrsak.NATURALYTELSE_BORTFALT;
            case NATURALYTELSE_TILKOMMER -> PeriodeÅrsak.NATURALYTELSE_TILKOMMER;
            case REFUSJON_AVSLÅTT -> PeriodeÅrsak.REFUSJON_AVSLÅTT;
            case REFUSJON_OPPHØRER -> PeriodeÅrsak.REFUSJON_OPPHØRER;
        };
    }

    private static DatoIntervall avklarBeregningsperiode(BeregningsgrunnlagAndelDto dto) {
        if (dto.beregningsperiodeTom() == null) {
            if (dto.beregningsperiodeFom() == null) {
                return null;
            }
            return DatoIntervall.fraOgMed(dto.beregningsperiodeFom());
        }
        return DatoIntervall.fraOgMedTilOgMed(dto.beregningsperiodeFom(), dto.beregningsperiodeTom());
    }

    private static Arbeidsgiver mapArbeidsgiverFraDto(BgAndelArbeidsforholdDto dto, UnaryOperator<String> hentNavn) {
        return new Arbeidsgiver(dto.arbeidsgiverIdent(), hentNavn.apply(dto.arbeidsgiverIdent()));
    }

    private static BeregningsgrunnlagPrStatusOgAndel mapBgpsaFraDto(BeregningsgrunnlagAndelDto dto, UnaryOperator<String> hentNavn) {
        BeregningsgrunnlagPrStatusOgAndel.Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = null;
        if (dto.arbeidsforhold() != null) {
            bgAndelArbeidsforhold = mapBgAndelArbeidsforholdfraDto(dto.arbeidsforhold(), hentNavn);
        }
        builder.medAktivitetStatus(mapAktivitetStatusFraDto(dto.aktivitetStatus()))
                .medBruttoPrÅr(dto.bruttoPrÅr())
                .medAvkortetPrÅr(dto.avkortetPrÅr())
                .medNyIArbeidslivet(dto.erNyIArbeidslivet())
                .medDagsats(dto.dagsats())
                .medBeregningsperiode(avklarBeregningsperiode(dto))
                .medBgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .medArbeidsforholdType(mapArbeidforholdTypeFraDto(dto.arbeidsforholdType()))
                .medErTilkommetAndel(dto.erTilkommetAndel());
        return builder.build();
    }

    static OpptjeningAktivitetType mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto arbeidsforholdType) {
        if (arbeidsforholdType == null) return OpptjeningAktivitetType.UDEFINERT;
        return switch (arbeidsforholdType) {
            case VENTELØNN_VARTPENGER -> OpptjeningAktivitetType.VENTELØNN_VARTPENGER;
            case DAGPENGER -> OpptjeningAktivitetType.DAGPENGER;
            case MILITÆR_ELLER_SIVILTJENESTE -> OpptjeningAktivitetType.MILITÆR_ELLER_SIVILTJENESTE;
            case UTENLANDSK_ARBEIDSFORHOLD -> OpptjeningAktivitetType.UTENLANDSK_ARBEIDSFORHOLD;
            case VIDERE_ETTERUTDANNING -> OpptjeningAktivitetType.VIDERE_ETTERUTDANNING;
            case ETTERLØNN_SLUTTPAKKE -> OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE;
            case UTDANNINGSPERMISJON -> OpptjeningAktivitetType.UTDANNINGSPERMISJON;
            case OPPLÆRINGSPENGER -> OpptjeningAktivitetType.OPPLÆRINGSPENGER;
            case ARBEID -> OpptjeningAktivitetType.ARBEID;
            case ARBEIDSAVKLARING -> OpptjeningAktivitetType.ARBEIDSAVKLARING;
            case FORELDREPENGER -> OpptjeningAktivitetType.FORELDREPENGER;
            case OMSORGSPENGER -> OpptjeningAktivitetType.OMSORGSPENGER;
            case PLEIEPENGER -> OpptjeningAktivitetType.PLEIEPENGER;
            case SYKEPENGER -> OpptjeningAktivitetType.SYKEPENGER;
            case FRISINN -> OpptjeningAktivitetType.FRISINN;
            case FRILANS -> OpptjeningAktivitetType.FRILANS;
            case NÆRING -> OpptjeningAktivitetType.NÆRING;
            case SVANGERSKAPSPENGER -> OpptjeningAktivitetType.SVANGERSKAPSPENGER;
        };
    }

    static AktivitetStatus mapAktivitetStatusFraDto(AktivitetStatusDto aktivitetStatus) {
        if (aktivitetStatus == null) return null;
        return switch (aktivitetStatus) {
            case ARBEIDSAVKLARINGSPENGER -> AktivitetStatus.ARBEIDSAVKLARINGSPENGER;
            case ARBEIDSTAKER -> AktivitetStatus.ARBEIDSTAKER;
            case BRUKERS_ANDEL -> AktivitetStatus.BRUKERS_ANDEL;
            case DAGPENGER -> AktivitetStatus.DAGPENGER;
            case FRILANSER -> AktivitetStatus.FRILANSER;
            case KOMBINERT_AT_FL -> AktivitetStatus.KOMBINERT_AT_FL;
            case KOMBINERT_AT_FL_SN -> AktivitetStatus.KOMBINERT_AT_FL_SN;
            case KOMBINERT_AT_SN -> AktivitetStatus.KOMBINERT_AT_SN;
            case KOMBINERT_FL_SN -> AktivitetStatus.KOMBINERT_FL_SN;
            case KUN_YTELSE -> AktivitetStatus.KUN_YTELSE;
            case MILITÆR_ELLER_SIVIL -> AktivitetStatus.MILITÆR_ELLER_SIVIL;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE;
            case TTLSTØTENDE_YTELSE -> AktivitetStatus.TTLSTØTENDE_YTELSE;
            case VENTELØNN_VARTPENGER -> AktivitetStatus.VENTELØNN_VARTPENGER;
        };
    }

    private static BGAndelArbeidsforhold mapBgAndelArbeidsforholdfraDto(BgAndelArbeidsforholdDto dto, UnaryOperator<String> hentNavn) {
        return new BGAndelArbeidsforhold(mapArbeidsgiverFraDto(dto, hentNavn),
                ArbeidsforholdRef.ref(dto.arbeidsforholdRef()),
                dto.naturalytelseBortfaltPrÅr(),
                dto.naturalytelseTilkommetPrÅr()
        );
    }

    private static BeregningsgrunnlagAktivitetStatus mapBeregningsgrunnlagAktivitetStatusFraDto(AktivitetStatusDto aktivitetStatus) {
        return new BeregningsgrunnlagAktivitetStatus(mapAktivitetStatusFraDto(aktivitetStatus));
    }
}

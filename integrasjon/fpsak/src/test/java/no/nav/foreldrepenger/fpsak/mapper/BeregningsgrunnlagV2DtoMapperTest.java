package no.nav.foreldrepenger.fpsak.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;

class BeregningsgrunnlagV2DtoMapperTest {

    @Test
    void mapHjemmelFraDto() {
        var hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_28_8_30);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_28_8_30);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_30);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_30);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_35);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_35);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_38);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_38);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_40);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_40);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_41);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_41);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_42);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_42);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_43);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_43);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_47);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_47);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_49);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_49);
        hjemmel = BeregningsgrunnlagV2DtoMapper.mapHjemmelFraDto(null);
        assertThat(hjemmel).isEqualTo(Hjemmel.UDEFINERT);
    }

    @Test
    void mapAktivitetStatus() {
        var status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.ARBEIDSTAKER);
        assertThat(status).isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KUN_YTELSE);
        assertThat(status).isEqualTo(AktivitetStatus.KUN_YTELSE);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_FL);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_FL);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_FL_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_FL_SN);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_SN);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_FL_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_FL_SN);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE);
        assertThat(status).isEqualTo(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.BRUKERS_ANDEL);
        assertThat(status).isEqualTo(AktivitetStatus.BRUKERS_ANDEL);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.DAGPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.DAGPENGER);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.FRILANSER);
        assertThat(status).isEqualTo(AktivitetStatus.FRILANSER);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.MILITÆR_ELLER_SIVIL);
        assertThat(status).isEqualTo(AktivitetStatus.MILITÆR_ELLER_SIVIL);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.TTLSTØTENDE_YTELSE);
        assertThat(status).isEqualTo(AktivitetStatus.TTLSTØTENDE_YTELSE);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.VENTELØNN_VARTPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.VENTELØNN_VARTPENGER);
        status = BeregningsgrunnlagV2DtoMapper.mapAktivitetStatusFraDto(null);
        assertThat(status).isNull();
    }

    @Test
    void mapPeriodeårsakFraDto() {
        var årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ARBEIDSFORHOLD_AVSLUTTET);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ARBEIDSFORHOLD_AVSLUTTET);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ENDRING_I_AKTIVITETER_SØKT_FOR);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ENDRING_I_AKTIVITETER_SØKT_FOR);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.GRADERING);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.GRADERING);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.GRADERING_OPPHØRER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.GRADERING_OPPHØRER);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ENDRING_I_REFUSJONSKRAV);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ENDRING_I_REFUSJONSKRAV);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.NATURALYTELSE_BORTFALT);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.NATURALYTELSE_BORTFALT);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.NATURALYTELSE_TILKOMMER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.NATURALYTELSE_TILKOMMER);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.REFUSJON_AVSLÅTT);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.REFUSJON_AVSLÅTT);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.REFUSJON_OPPHØRER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.REFUSJON_OPPHØRER);
        årsak = BeregningsgrunnlagV2DtoMapper.mapPeriodeårsakFraDto(null);
        assertThat(årsak).isNull();
    }

    @Test
    void mapArbeidforholdTypeFraDto() {
        var opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.SVANGERSKAPSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.SVANGERSKAPSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ARBEID);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ARBEID);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.DAGPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.DAGPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ARBEIDSAVKLARING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ARBEIDSAVKLARING);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.UTENLANDSK_ARBEIDSFORHOLD);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UTENLANDSK_ARBEIDSFORHOLD);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ETTERLØNN_SLUTTPAKKE);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FORELDREPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FORELDREPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FRILANS);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FRILANS);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FRISINN);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FRISINN);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.MILITÆR_ELLER_SIVILTJENESTE);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.MILITÆR_ELLER_SIVILTJENESTE);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.NÆRING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.NÆRING);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.OMSORGSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.OMSORGSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.PLEIEPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.PLEIEPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.OPPLÆRINGSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.OPPLÆRINGSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.SYKEPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.SYKEPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.UTDANNINGSPERMISJON);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UTDANNINGSPERMISJON);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.VENTELØNN_VARTPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.VENTELØNN_VARTPENGER);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.VIDERE_ETTERUTDANNING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.VIDERE_ETTERUTDANNING);
        opptjeningAktivitet = BeregningsgrunnlagV2DtoMapper.mapArbeidforholdTypeFraDto(null);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UDEFINERT);
    }
}
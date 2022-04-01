package no.nav.foreldrepenger.fpsak.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.OpptjeningAktivitetDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;

class BeregningsgrunnlagDtoMapperTest {

    @Test
    void mapHjemmelFraDto() {
        var hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_28_8_30);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_28_8_30);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_30);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_30);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_35);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_35);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_38);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_38);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_40);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_40);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_41);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_41);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_42);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_42);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_43);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_43);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_47);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_47);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(HjemmelDto.F_14_7_8_49);
        assertThat(hjemmel).isEqualTo(Hjemmel.F_14_7_8_49);
        hjemmel = BeregningsgrunnlagDtoMapper.mapHjemmelFraDto(null);
        assertThat(hjemmel).isEqualTo(Hjemmel.UDEFINERT);
    }

    @Test
    void mapAktivitetStatus() {
        var status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.ARBEIDSTAKER);
        assertThat(status).isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KUN_YTELSE);
        assertThat(status).isEqualTo(AktivitetStatus.KUN_YTELSE);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_FL);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_FL);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_FL_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_FL_SN);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_AT_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_AT_SN);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.KOMBINERT_FL_SN);
        assertThat(status).isEqualTo(AktivitetStatus.KOMBINERT_FL_SN);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.SELVSTENDIG_NÆRINGSDRIVENDE);
        assertThat(status).isEqualTo(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.ARBEIDSAVKLARINGSPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.ARBEIDSAVKLARINGSPENGER);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.BRUKERS_ANDEL);
        assertThat(status).isEqualTo(AktivitetStatus.BRUKERS_ANDEL);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.DAGPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.DAGPENGER);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.FRILANSER);
        assertThat(status).isEqualTo(AktivitetStatus.FRILANSER);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.MILITÆR_ELLER_SIVIL);
        assertThat(status).isEqualTo(AktivitetStatus.MILITÆR_ELLER_SIVIL);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.TTLSTØTENDE_YTELSE);
        assertThat(status).isEqualTo(AktivitetStatus.TTLSTØTENDE_YTELSE);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(AktivitetStatusDto.VENTELØNN_VARTPENGER);
        assertThat(status).isEqualTo(AktivitetStatus.VENTELØNN_VARTPENGER);
        status = BeregningsgrunnlagDtoMapper.mapAktivitetStatusFraDto(null);
        assertThat(status).isNull();
    }

    @Test
    void mapPeriodeårsakFraDto() {
        var årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ARBEIDSFORHOLD_AVSLUTTET);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ARBEIDSFORHOLD_AVSLUTTET);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ENDRING_I_AKTIVITETER_SØKT_FOR);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ENDRING_I_AKTIVITETER_SØKT_FOR);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.GRADERING);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.GRADERING);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.GRADERING_OPPHØRER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.GRADERING_OPPHØRER);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.ENDRING_I_REFUSJONSKRAV);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.ENDRING_I_REFUSJONSKRAV);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.NATURALYTELSE_BORTFALT);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.NATURALYTELSE_BORTFALT);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.NATURALYTELSE_TILKOMMER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.NATURALYTELSE_TILKOMMER);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.REFUSJON_AVSLÅTT);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.REFUSJON_AVSLÅTT);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(PeriodeÅrsakDto.REFUSJON_OPPHØRER);
        assertThat(årsak).isEqualTo(PeriodeÅrsak.REFUSJON_OPPHØRER);
        årsak = BeregningsgrunnlagDtoMapper.mapPeriodeårsakFraDto(null);
        assertThat(årsak).isNull();
    }

    @Test
    void mapArbeidforholdTypeFraDto() {
        var opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.SVANGERSKAPSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.SVANGERSKAPSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ARBEID);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ARBEID);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.DAGPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.DAGPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ARBEIDSAVKLARING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ARBEIDSAVKLARING);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.UTENLANDSK_ARBEIDSFORHOLD);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UTENLANDSK_ARBEIDSFORHOLD);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.ETTERLØNN_SLUTTPAKKE);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FORELDREPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FORELDREPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FRILANS);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FRILANS);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.FRISINN);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.FRISINN);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.MILITÆR_ELLER_SIVILTJENESTE);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.MILITÆR_ELLER_SIVILTJENESTE);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.NÆRING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.NÆRING);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.OMSORGSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.OMSORGSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.PLEIEPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.PLEIEPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.OPPLÆRINGSPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.OPPLÆRINGSPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.SYKEPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.SYKEPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.UTDANNINGSPERMISJON);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UTDANNINGSPERMISJON);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.VENTELØNN_VARTPENGER);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.VENTELØNN_VARTPENGER);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(OpptjeningAktivitetDto.VIDERE_ETTERUTDANNING);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.VIDERE_ETTERUTDANNING);
        opptjeningAktivitet = BeregningsgrunnlagDtoMapper.mapArbeidforholdTypeFraDto(null);
        assertThat(opptjeningAktivitet).isEqualTo(OpptjeningAktivitetType.UDEFINERT);
    }
}
package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapperTest {

    private ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper foreldrepengerInfoTilAnnenForeldrerDokumentdataMapper;

    @BeforeEach
    void setUp() {
        foreldrepengerInfoTilAnnenForeldrerDokumentdataMapper = new ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper();
    }

    @Test
    void mapInfoTilAnnenForelder() {
        // Arrange
        var periode1Fom = LocalDate.now();
        var periode1Tom = LocalDate.now().plusDays(20);
        var periode2Fom = LocalDate.now().plusDays(20);
        var periode2Tom = LocalDate.now().plusDays(40);

        var foreldrepengerUttakData = lagForeldrepengerUttak(periode1Fom, periode1Tom, periode2Fom, periode2Tom);
        var behandling = defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingÅrsakTyper(List.of(BrevGrunnlagDto.BehandlingÅrsakType.INFOBREV_BEHANDLING))
            .foreldrepenger(foreldrepengerUttakData)
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).build();

        //Act
        var infoTilAnnenForelderData = foreldrepengerInfoTilAnnenForeldrerDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse,
            behandling, false);

        //assert
        assertThat(infoTilAnnenForelderData.getBehandlingsÅrsak()).isEqualTo(BehandlingÅrsakType.INFOBREV_BEHANDLING);
        assertThat(infoTilAnnenForelderData.getSisteUttaksdagMor()).isEqualTo(formaterDato(periode2Tom, dokumentFelles.getSpråkkode()));
    }

    @Test
    void mapInfoTilAnnenForelderOpphold() {
        // Arrange
        var periode1Fom = LocalDate.now();
        var periode1Tom = LocalDate.now().plusDays(20);
        var periode2Fom = LocalDate.now().plusDays(20);
        var periode2Tom = LocalDate.now().plusDays(30);

        var foreldrepengerUttakData = lagForeldrepengerUttak(periode1Fom, periode1Tom, periode2Fom, periode2Tom);
        var behandling = defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingÅrsakTyper(List.of(BrevGrunnlagDto.BehandlingÅrsakType.INFOBREV_OPPHOLD))
            .foreldrepenger(foreldrepengerUttakData)
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).build();

        //Act
        var infoTilAnnenForelderData = foreldrepengerInfoTilAnnenForeldrerDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse,
            behandling, false);

        //assert
        assertThat(infoTilAnnenForelderData.getBehandlingsÅrsak()).isEqualTo(BehandlingÅrsakType.INFOBREV_OPPHOLD);
        assertThat(infoTilAnnenForelderData.getSisteUttaksdagMor()).isNull();
    }

    private BrevGrunnlagDto.Foreldrepenger lagForeldrepengerUttak(LocalDate periode1Fom, LocalDate periode1Tom,
                                                                  LocalDate periode2Fom, LocalDate periode2Tom) {
        var periode1 = foreldrepengerUttakPeriode()
            .fom(periode1Fom)
            .tom(periode1Tom)
            .aktiviteter(List.of())
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.UKJENT.getKode())
            .build();
        var periode2 = foreldrepengerUttakPeriode()
            .fom(periode2Fom)
            .tom(periode2Tom)
            .aktiviteter(List.of())
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.UKJENT.getKode())
            .build();

        return foreldrepenger()
            .stønadskontoer(List.of())
            .tapteDagerFpff(0)
            .perioderSøker(List.of(periode1, periode2))
            .perioderAnnenpart(List.of(periode1, periode2))
            .ønskerJustertUttakVedFødsel(false)
            .build();
    }
}

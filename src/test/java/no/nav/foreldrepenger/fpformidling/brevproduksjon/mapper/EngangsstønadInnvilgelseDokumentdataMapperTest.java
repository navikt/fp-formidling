package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.originalBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.tilkjentYtelse;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

@ExtendWith(MockitoExtension.class)
class EngangsstønadInnvilgelseDokumentdataMapperTest {
    private EngangsstønadInnvilgelseDokumentdataMapper dokumentdataMapperTest;

    @BeforeEach
    void setup() {
        dokumentdataMapperTest = new EngangsstønadInnvilgelseDokumentdataMapper(DatamapperTestUtil.getBrevParametere());
    }

    @Test
    void case_med_endret_sats_blir_satt_riktig() {
        //Arrange
        var familieHendelse = lagFamHendelse(1);
        var orgfamilieHendelse = lagFamHendelse(1);
        var orgTilkjentYtelse = new TilkjentYtelseEngangsstønadDto(86000L);
        var tilkjentYtelseDto = new TilkjentYtelseEngangsstønadDto(85000L);

        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.REVURDERING)
            .familieHendelse(familieHendelse)
            .tilkjentYtelse(tilkjentYtelse().engangsstønad(tilkjentYtelseDto).originalBehandlingEngangsstønad(orgTilkjentYtelse).build())
            .originalBehandling(originalBehandling().familieHendelse(orgfamilieHendelse).build())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isTrue();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(1000L));
        assertThat(innvilgelseDokumentdata.getDød()).isFalse();
        assertThat(innvilgelseDokumentdata.getFbEllerMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isFalse();
    }

    @Test
    void skal_ikke_flagge_endret_sats_hvis_forrige_behandling_manglet_tilkjent_ytelse() {
        //Arrange
        var familieHendelse = lagFamHendelse(1);
        var tilkjentYtelseDto = new TilkjentYtelseEngangsstønadDto(85000L);

        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.REVURDERING)
            .familieHendelse(familieHendelse)
            .tilkjentYtelse(tilkjentYtelse().engangsstønad(tilkjentYtelseDto).build())
            .originalBehandling(originalBehandling().familieHendelse(familieHendelse).build())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(85000L));
    }

    @Test
    void skal_sende_original_til_verge() {
        //Arrange
        var tilkjentYtelseDto = new TilkjentYtelseEngangsstønadDto(85000L);
        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(lagFamHendelse(1))
            .tilkjentYtelse(tilkjentYtelse().engangsstønad(tilkjentYtelseDto).build())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.NEI, true, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
    }

    @Test
    void skal_sende_kopi_til_søker() {
        //Arrange
        var tilkjentYtelseDto = new TilkjentYtelseEngangsstønadDto(85000L);
        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(lagFamHendelse(1))
            .tilkjentYtelse(tilkjentYtelse().engangsstønad(tilkjentYtelseDto).build())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.JA, false, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getMottakerNavn()).isNull();
    }

    @Test
    void endring_antall_barn_ikke_endretSats() {
        //Arrange
        var familieHendelse = lagFamHendelse(2);
        var orgfamilieHendelse = lagFamHendelse(1);
        var orgTilkjentYtelse = new TilkjentYtelseEngangsstønadDto(86000L);
        var tilkjentYtelseDto = new TilkjentYtelseEngangsstønadDto(85000L);

        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.REVURDERING)
            .familieHendelse(familieHendelse)
            .tilkjentYtelse(tilkjentYtelse().engangsstønad(tilkjentYtelseDto).originalBehandlingEngangsstønad(orgTilkjentYtelse).build())
            .originalBehandling(originalBehandling().familieHendelse(orgfamilieHendelse).build())
            .build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(1000L));
    }

    private BrevGrunnlagDto.FamilieHendelse lagFamHendelse(int antallBarn) {
        return familieHendelse().antallBarn(antallBarn).termindato(LocalDate.now()).build();
    }

    private String formaterBeløp(long beløp) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("nb-NO")).format(beløp);
    }
}

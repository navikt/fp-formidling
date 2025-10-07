package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.Fagsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;

@ExtendWith(MockitoExtension.class)
class EngangsstønadInnvilgelseDokumentdataMapperTest {
    private EngangsstønadInnvilgelseDokumentdataMapper dokumentdataMapperTest;
    private static final UUID ID = UUID.randomUUID();
    private static final UUID ID_REV = UUID.randomUUID();

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentHendelse dokumentHendelse;

    @BeforeEach
    void setup() {
        dokumentHendelse = lagStandardHendelseBuilder().build();
        dokumentdataMapperTest = new EngangsstønadInnvilgelseDokumentdataMapper(DatamapperTestUtil.getBrevParametere(), domeneobjektProvider);
    }

    @Test
    void case_med_endret_sats_blir_satt_riktig() {
        //Arrange
        var familieHendelse = lagFamHendelse(1);
        var orgfamilieHendelse = lagFamHendelse(1);
        var orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID, orgfamilieHendelse);
        var innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV, familieHendelse);

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);


        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(innvilgetES)).thenReturn(new TilkjentYtelseEngangsstønad(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(innvilgetES)).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentTilkjentYtelseESHvisFinnes(orgBehES)).thenReturn(Optional.of(new TilkjentYtelseEngangsstønad(86000L)));

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

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
        var orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID, lagFamHendelse(1));
        var innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV, lagFamHendelse(1));
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);

        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(innvilgetES)).thenReturn(new TilkjentYtelseEngangsstønad(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(innvilgetES)).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentTilkjentYtelseESHvisFinnes(orgBehES)).thenReturn(Optional.empty());

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(85000L));
    }

    @Test
    void skal_sende_original_til_verge() {
        //Arrange
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.NEI, true, FagsakYtelseType.FORELDREPENGER);
        var innvilgetES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID_REV, lagFamHendelse(1));

        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(innvilgetES)).thenReturn(new TilkjentYtelseEngangsstønad(85000L));

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
    }

    @Test
    void skal_sende_kopi_til_søker() {
        //Arrange
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.JA, false, FagsakYtelseType.FORELDREPENGER);
        var innvilgetES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID_REV, lagFamHendelse(1));

        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(innvilgetES)).thenReturn(new TilkjentYtelseEngangsstønad(85000L));

        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

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
        var orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID, orgfamilieHendelse);
        var innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV, familieHendelse);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);

        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(innvilgetES)).thenReturn(new TilkjentYtelseEngangsstønad(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(innvilgetES)).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentTilkjentYtelseESHvisFinnes(orgBehES)).thenReturn(Optional.of(new TilkjentYtelseEngangsstønad(86000L)));
        //Act
        var innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(1000L));
    }

    private Behandling opprettBehandling(BehandlingType behType, UUID id, FamilieHendelse familieHendelse) {
        return Behandling.builder()
            .medUuid(id)
            .medBehandlingType(behType)
            .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
            .medFagsak(Fagsak.ny().medYtelseType(FagsakYtelseType.ENGANGSTØNAD).build())
            .medFamilieHendelse(familieHendelse)
            .build();
    }

    private FamilieHendelse lagFamHendelse(int antallBarn) {
        return new FamilieHendelse(List.of(), LocalDate.now(), antallBarn, null);
    }

    private String formaterBeløp(long beløp) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("nb-NO")).format(beløp);
    }
}

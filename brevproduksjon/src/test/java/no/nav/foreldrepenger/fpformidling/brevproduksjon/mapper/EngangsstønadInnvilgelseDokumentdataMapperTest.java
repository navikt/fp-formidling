package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
class EngangsstønadInnvilgelseDokumentdataMapperTest {
    private EngangsstønadInnvilgelseDokumentdataMapper dokumentdataMapperTest;
    private static final UUID ID = UUID.randomUUID();
    private static final UUID ID_REV = UUID.randomUUID();

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentHendelse dokumentHendelse;

    private DokumentFelles dokumentFelles;

    @BeforeEach
    public void setup() {
        dokumentHendelse = lagStandardHendelseBuilder().build();
        dokumentdataMapperTest = new EngangsstønadInnvilgelseDokumentdataMapper(DatamapperTestUtil.getBrevParametere(), domeneobjektProvider);
    }

    @Test
    public void case_med_endret_sats_blir_satt_riktig() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);

        dokumentFelles = lagStandardDokumentFelles(lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE));

        FamilieHendelse familieHendelse = lagFamHendelse(BigInteger.ONE);
        FamilieHendelse orgfamilieHendelse = lagFamHendelse(BigInteger.ONE);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.of(new BeregningsresultatES(86000L)));

        when(domeneobjektProvider.hentFamiliehendelse(eq(innvilgetES))).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentFamiliehendelse(eq(orgBehES))).thenReturn(orgfamilieHendelse);

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

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
    public void skal_ikke_flagge_endret_sats_hvis_forrige_behandling_manglet_beregningsresultat() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        dokumentFelles = lagStandardDokumentFelles(lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE));

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.empty());

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(85000L));
    }

    @Test
    public void skal_sende_original_til_verge() {
        //Arrange
        dokumentFelles = lagStandardDokumentFelles(lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE), DokumentFelles.Kopi.NEI, true);
        Behandling innvilgetES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID_REV);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
    }

    @Test
    public void skal_sende_kopi_til_søker() {
        //Arrange
        dokumentFelles = lagStandardDokumentFelles(lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE), DokumentFelles.Kopi.JA, false);
        Behandling innvilgetES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID_REV);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getMottakerNavn()).isNull();
    }

    @Test
    public void endring_antall_barn_ikke_endretSats() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        FamilieHendelse familieHendelse = lagFamHendelse(BigInteger.TWO);
        FamilieHendelse orgfamilieHendelse = lagFamHendelse(BigInteger.ONE);
        dokumentFelles = lagStandardDokumentFelles(lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE));

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.of(new BeregningsresultatES(86000L)));

        when(domeneobjektProvider.hentFamiliehendelse(eq(innvilgetES))).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentFamiliehendelse(eq(orgBehES))).thenReturn(orgfamilieHendelse);
        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES, false);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp()).isEqualTo(formaterBeløp(1000L));
    }

    private Behandling opprettBehandling(BehandlingType behType, UUID id) {
        return Behandling.builder()
                .medUuid(id)
                .medBehandlingType(behType)
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private FamilieHendelse lagFamHendelse(BigInteger antallBarn) {
        return new FamilieHendelse(antallBarn, 0, true, true, FamilieHendelseType.TERMIN,
                new FamilieHendelse.OptionalDatoer(Optional.of(LocalDate.now()), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    private String formaterBeløp(long beløp) {
        return String.format("%,d", beløp);
    }
}

package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InnvilgelseEngangstønadDokumentdataMapperTest {
    private InnvilgelseEngangstønadDokumentdataMapper dokumentdataMapperTest;
    private static final long ID = 123L;
    private static final long ID_REV = 124L;
    private static final String VERGE_NAVN = "Finn Verge";

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);
    @Mock
    private DokumentHendelse dokumentHendelse = mock(DokumentHendelse.class);

    private DokumentFelles dokumentFelles;

    @BeforeEach
    public void setup() {
        dokumentdataMapperTest = new InnvilgelseEngangstønadDokumentdataMapper(DatamapperTestUtil.getBrevParametere(), domeneobjektProvider);
    }

    @Test
    public void case_med_endret_sats_blir_satt_riktig() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);

        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagDokumentData(), null, null);

        FamilieHendelse familieHendelse = lagFamHendelse(BigInteger.ONE);
        FamilieHendelse orgfamilieHendelse = lagFamHendelse(BigInteger.ONE);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.of(new BeregningsresultatES(86000L)));

        when(domeneobjektProvider.hentFamiliehendelse(eq(innvilgetES))).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentFamiliehendelse(eq(orgBehES))).thenReturn(orgfamilieHendelse);

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isTrue();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp().equals("1 000"));
        assertThat(innvilgelseDokumentdata.getDød()).isFalse();
        assertThat(innvilgelseDokumentdata.getFbEllerMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.felles.getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.felles.getHarVerge()).isFalse();
    }

    @Test
    public void skal_ikke_flagge_endret_sats_hvis_forrige_behandling_manglet_beregningsresultat() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagDokumentData(), null, null);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.empty());

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp().equals("85 000"));
    }

    @Test
    public void case_med_verge_sender_MottakerNavn() {
        //Arrange
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagDokumentData(), VERGE_NAVN, DokumentFelles.Kopi.JA);
        Behandling innvilgetES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID_REV);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innvilgelseDokumentdata.felles.getMottakerNavn().equals(VERGE_NAVN));
    }

    @Test
    public void endring_antall_barn_ikke_endretSats() {
        //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        FamilieHendelse familieHendelse = lagFamHendelse(BigInteger.TWO);
        FamilieHendelse orgfamilieHendelse = lagFamHendelse(BigInteger.ONE);
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagDokumentData(), null, DokumentFelles.Kopi.NEI);

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.of(new BeregningsresultatES(86000L)));

        when(domeneobjektProvider.hentFamiliehendelse(eq(innvilgetES))).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentFamiliehendelse(eq(orgBehES))).thenReturn(orgfamilieHendelse);
        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp().equals("1 000"));
    }

    private Behandling opprettBehandling(BehandlingType behType, long id) {
        return Behandling.builder().medId(id)
                .medBehandlingType(behType)
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private FamilieHendelse lagFamHendelse(BigInteger antallBarn) {
        return new FamilieHendelse(antallBarn, true, true, FamilieHendelseType.TERMIN,
                new FamilieHendelse.OptionalDatoer(Optional.of(LocalDate.now()), Optional.empty(), Optional.empty(), Optional.empty()));
    }
}
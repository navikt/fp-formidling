package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.StartdatoUtsattDto;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerAnnullertDokumentdataMapperTest {

    private static final LocalDate NY_STARTDATO = LocalDate.now().plusWeeks(20);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private ForeldrepengerAnnullertDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new ForeldrepengerAnnullertDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker_med_ny_startdato() {
        // Arrange
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
        when(domeneobjektProvider.hentStartdatoUtsatt(behandling)).thenReturn(new StartdatoUtsattDto(true, NY_STARTDATO));

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
        assertThat(dokumentdata.getHarSøktOmNyPeriode()).isTrue();
        assertThat(dokumentdata.getPlanlagtOppstartDato()).isEqualTo(formaterDatoNorsk(NY_STARTDATO));
        assertThat(dokumentdata.getKanBehandlesDato()).isEqualTo(formaterDatoNorsk(NY_STARTDATO.minusWeeks(4)));
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker_uten_ny_startdato() {
        // Arrange
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
        when(domeneobjektProvider.hentStartdatoUtsatt(behandling)).thenReturn(new StartdatoUtsattDto(true, null));

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getHarSøktOmNyPeriode()).isFalse();
        assertThat(dokumentdata.getPlanlagtOppstartDato()).isNull();
        assertThat(dokumentdata.getKanBehandlesDato()).isNull();
    }
}

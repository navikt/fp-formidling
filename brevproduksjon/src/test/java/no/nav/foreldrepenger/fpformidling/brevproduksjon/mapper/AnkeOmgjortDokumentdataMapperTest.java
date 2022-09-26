package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.anke.Anke;
import no.nav.foreldrepenger.fpformidling.anke.AnkeVurderingOmgjør;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.AnkeOmgjortDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class AnkeOmgjortDokumentdataMapperTest {

    private static final String FRITEKST = "Fritekst i brevet";
    private static final String ALTERNATIV_FRITEKST = "Alternativ fritekst";
    private static final UUID PÅANKET_BEHANDLING_UUID = UUID.randomUUID();
    private static final LocalDate VEDTAKSDATO = LocalDate.now().minusDays(2);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private AnkeOmgjortDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.ANKE_OMGJORT);
        dokumentdataMapper = new AnkeOmgjortDokumentdataMapper(domeneobjektProvider);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        mockDomeneobjektProvider(true, FRITEKST);
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));

        assertThat(dokumentdata.getGunst()).isTrue();
        assertThat(dokumentdata.getVedtaksdato()).isEqualTo(formaterDatoNorsk(VEDTAKSDATO));
    }

    @Test
    public void skal_mappe_ugunst() {
        // Arrange
        mockDomeneobjektProvider(false, FRITEKST);
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getGunst()).isFalse();
    }

    @Test
    public void skal_hente_fritekst_fra_hendelse_når_den_ikke_kommer_i_anke() {
        // Arrange
        mockDomeneobjektProvider(false, null);
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().medFritekst(ALTERNATIV_FRITEKST).build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(ALTERNATIV_FRITEKST));
    }

    private void mockDomeneobjektProvider(boolean gunst, String fritekst) {
        var anke = Anke.ny()
                .medFritekstTilBrev(fritekst)
                .medAnkeVurderingOmgjoer(gunst ? AnkeVurderingOmgjør.ANKE_TIL_GUNST : AnkeVurderingOmgjør.ANKE_TIL_UGUNST)
                .medPaAnketBehandlingUuid(PÅANKET_BEHANDLING_UUID)
                .build();
        when(domeneobjektProvider.hentAnkebehandling(any(Behandling.class))).thenReturn(Optional.of(anke));

        var påanketBehandling = Behandling.builder().medOriginalVedtaksDato(VEDTAKSDATO).build();
        when(domeneobjektProvider.hentBehandling(eq(PÅANKET_BEHANDLING_UUID))).thenReturn(påanketBehandling);
    }
}

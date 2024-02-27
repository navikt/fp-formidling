package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalEnum;

@ExtendWith(MockitoExtension.class)
class ForlengetSaksbehandlingstidDokumentdataMapperTest {

    private static final int ANTALL_BARN = 2;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForlengetSaksbehandlingstidDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        dokumentData = DatamapperTestUtil.lagStandardDokumentData(DokumentMalEnum.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new ForlengetSaksbehandlingstidDokumentdataMapper(domeneobjektProvider);

        var familieHendelse = opprettFamiliehendelse();
        when(domeneobjektProvider.hentFamiliehendelseHvisFinnes(any(Behandling.class))).thenReturn(Optional.of(familieHendelse));
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medDokumentMal(
            no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum.FORLENGET_SAKSBEHANDLINGSTID_MEDL).build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDato(LocalDate.now(), behandling.getSpråkkode()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getVariantType()).isEqualTo(ForlengetSaksbehandlingstidDokumentdata.VariantType.MEDLEM);
        assertThat(dokumentdata.getDød()).isFalse();
        assertThat(dokumentdata.getBehandlingsfristUker()).isEqualTo(6);
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(FamilieHendelseType.TERMIN, ANTALL_BARN, 0, null, null, null, null, false, false);
    }
}

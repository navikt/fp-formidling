package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class IkkeSøktDokumentdataMapperTest {

    private static final String ARBEIDSGIVER = "Arbeidsgiver AS";
    private static final LocalDate INNSENDINGSTIDSPUNKT = LocalDate.now().minusDays(2);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private IkkeSøktDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new IkkeSøktDokumentdataMapper(domeneobjektProvider);

        Inntektsmelding inntektsmelding = new Inntektsmelding(ARBEIDSGIVER, "", null, INNSENDINGSTIDSPUNKT);
        InntektArbeidYtelse iay = InntektArbeidYtelse.ny().medInntektsmeldinger(List.of(inntektsmelding)).build();
        when(domeneobjektProvider.hentInntektArbeidYtelse(any(Behandling.class))).thenReturn(iay);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        IkkeSøktDokumentdata ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(ikkeSøktDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(ikkeSøktDokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(ikkeSøktDokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(ikkeSøktDokumentdata.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER);
        assertThat(ikkeSøktDokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(INNSENDINGSTIDSPUNKT));
    }

    @Test
    public void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.NEI, true);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        IkkeSøktDokumentdata ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isEqualTo(false);
    }
}
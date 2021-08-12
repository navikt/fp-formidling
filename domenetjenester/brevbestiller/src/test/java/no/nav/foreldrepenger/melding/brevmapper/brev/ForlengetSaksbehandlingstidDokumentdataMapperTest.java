package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class ForlengetSaksbehandlingstidDokumentdataMapperTest {

    private static final int ANTALL_BARN = 2;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForlengetSaksbehandlingstidDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new ForlengetSaksbehandlingstidDokumentdataMapper(domeneobjektProvider);

        FamilieHendelse familieHendelse = opprettFamiliehendelse();
        when(domeneobjektProvider.hentFamiliehendelseHvisFinnes(any(Behandling.class))).thenReturn(Optional.of(familieHendelse));
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder()
                .medDokumentMalType(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL)
                .build();

        // Act
        ForlengetSaksbehandlingstidDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDato(LocalDate.now(), behandling.getSpråkkode()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getVariantType()).isEqualTo(ForlengetSaksbehandlingstidDokumentdata.VariantType.MEDLEM);
        assertThat(dokumentdata.getDød()).isFalse();
        assertThat(dokumentdata.getBehandlingsfristUker()).isEqualTo(6);
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(ANTALL_BARN), false, false, FamilieHendelseType.TERMIN, optionalDatoer);
    }
}
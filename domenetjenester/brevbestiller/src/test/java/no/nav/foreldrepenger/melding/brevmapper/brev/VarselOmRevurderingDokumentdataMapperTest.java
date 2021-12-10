package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles.Kopi;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VarselOmRevurderingDokumentdataMapperTest {

    private static final LocalDate TERMINDATO = LocalDate.now();
    private static final int ANTALL_BARN = 1;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BrevMapperUtil brevMapperUtil;

    private DokumentData dokumentData;

    private VarselOmRevurderingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentData = lagStandardDokumentData(DokumentMalType.VARSEL_OM_REVURDERING);
        dokumentdataMapper = new VarselOmRevurderingDokumentdataMapper(brevMapperUtil, domeneobjektProvider);

        FamilieHendelse familieHendelse = opprettFamiliehendelse();
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(familieHendelse);
        when(domeneobjektProvider.kreverSammenhengendeUttak(any())).thenReturn(true);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

        // Act
        VarselOmRevurderingDokumentdata varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFelles()).isNotNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(varselOmRevurderingDokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(varselOmRevurderingDokumentdata.getTerminDato()).isEqualTo(formaterDatoNorsk(TERMINDATO));
        assertThat(varselOmRevurderingDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
        assertThat(varselOmRevurderingDokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
        assertThat(varselOmRevurderingDokumentdata.getAdvarselKode()).isEqualTo(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN.getKode());
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isFalse();
        assertThat(varselOmRevurderingDokumentdata.getKreverSammenhengendeUttak()).isTrue();
    }

    @Test
    public void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, true);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

        // Act
        VarselOmRevurderingDokumentdata varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFelles()).isNotNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErKopi()).isEqualTo(false);
    }

    @Test
    public void skal_gi_flere_opplysninger_når_ikke_JOBBFULLTID_er_årasak() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEID_I_UTLANDET);

        // Act
        VarselOmRevurderingDokumentdata varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isTrue();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.of(TERMINDATO), Optional.empty(), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(ANTALL_BARN), 0, false, false, FamilieHendelseType.TERMIN, optionalDatoer);
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER).build()))
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private DokumentHendelse lagDokumentHendelse(RevurderingVarslingÅrsak varslingÅrsak) {
        return lagStandardHendelseBuilder()
                .medRevurderingVarslingÅrsak(varslingÅrsak)
                .build();
    }
}

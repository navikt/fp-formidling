package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.typer.JournalpostId;

class InnsynDokumentdataMapperTest {

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private InnsynDokumentdataMapper innsynDokumentdataMapper;

    private List<InnsynDokument> innsynDokumentList = new ArrayList<>();
    Behandling behandling;
    private static final String FRITEKST = "Årsak til avvist er at det ikke mulig\ndet er fordi vi ikke har nok informasjon";

    @BeforeEach
    void setUp() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNSYN_SVAR);
        var dokument1 = new InnsynDokument(new JournalpostId(124L), "1");
        var dokument2 = new InnsynDokument(new JournalpostId(125L), "2");
        innsynDokumentList = List.of(dokument1, dokument2);
        behandling = opprettBehandling();

        innsynDokumentdataMapper = new InnsynDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    void mappingAvInnsynInnvilgetForeldrePenger() {
        var innsynsBehandling = new Innsyn(InnsynResultatType.INNVILGET, innsynDokumentList);
        when(domeneobjektProvider.hentInnsyn(behandling)).thenReturn(innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.FORELDREPENGER);

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.INNVILGET.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(innsynsDokumentData.getKlagefrist()).isEqualTo(6);
    }

    @Test
    void mappingAvInnsynAvvistEngangsstønadMedFritekst() {
        var innsynsBehandling = new Innsyn(InnsynResultatType.AVVIST, innsynDokumentList);
        when(domeneobjektProvider.hentInnsyn(behandling)).thenReturn(innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.ENGANGSTØNAD);

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.AVVIST.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD.getKode());
        assertThat(innsynsDokumentData.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.INNSYN)
            .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
            .build();
    }

    private DokumentHendelse lagDokumentHendelse(FagsakYtelseType ytelseType) {
        return lagStandardHendelseBuilder().medYtelseType(ytelseType).medFritekst(FRITEKST).build();
    }
}

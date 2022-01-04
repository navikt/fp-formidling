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

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

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
        BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNSYN_SVAR);
        InnsynDokument dokument1 = new InnsynDokument(new JournalpostId(124L), "1");
        InnsynDokument dokument2 = new InnsynDokument(new JournalpostId(125L), "2");
        innsynDokumentList = List.of(dokument1, dokument2);
        behandling = opprettBehandling();

        innsynDokumentdataMapper = new InnsynDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    public void mappingAvInnsynInnvilgetForeldrePenger() {
        Innsyn innsynsBehandling = new Innsyn(InnsynResultatType.INNVILGET, innsynDokumentList);
        when(domeneobjektProvider.hentInnsyn(behandling)).thenReturn(innsynsBehandling);

        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.FORELDREPENGER);

        InnsynDokumentdata innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.INNVILGET.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(innsynsDokumentData.getKlagefrist()).isEqualTo(6);
    }

    @Test
    public void mappingAvInnsynAvvistEngangsstønadMedFritekst() {
        Innsyn innsynsBehandling = new Innsyn(InnsynResultatType.AVVIST, innsynDokumentList);
        when(domeneobjektProvider.hentInnsyn(behandling)).thenReturn(innsynsBehandling);

        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.ENGANGSTØNAD);

        InnsynDokumentdata innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.AVVIST.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD.getKode());
        assertThat(innsynsDokumentData.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.INNSYN)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private DokumentHendelse lagDokumentHendelse(FagsakYtelseType ytelseType) {
        return lagStandardHendelseBuilder()
                .medYtelseType(ytelseType)
                .medFritekst(FRITEKST)
                .build();
    }
}

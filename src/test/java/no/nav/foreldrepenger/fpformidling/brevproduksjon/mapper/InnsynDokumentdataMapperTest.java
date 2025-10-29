package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.InnsynBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.InnsynBehandling.InnsynDokument;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

class InnsynDokumentdataMapperTest {

    private static final String FRITEKST = "Årsak til avvist er at det ikke mulig\ndet er fordi vi ikke har nok informasjon";

    private InnsynDokumentdataMapper innsynDokumentdataMapper;

    private List<InnsynDokument> innsynDokumentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        var dokument1 = new InnsynDokument("124", "1");
        var dokument2 = new InnsynDokument("125", "2");
        innsynDokumentList = List.of(dokument1, dokument2);

        innsynDokumentdataMapper = new InnsynDokumentdataMapper(brevParametere);
    }

    @Test
    void mappingAvInnsynInnvilgetForeldrePenger() {
        var innsynsBehandling = new InnsynBehandling(InnsynBehandling.InnsynResultatType.INNVILGET, innsynDokumentList);
        var behandling = opprettBehandling(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER, innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynDokumentdata.InnsynResultatType.INNV);
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(innsynsDokumentData.getKlagefrist()).isEqualTo(6);
    }

    @Test
    void mappingAvInnsynAvvistEngangsstønadMedFritekst() {
        var innsynsBehandling = new InnsynBehandling(InnsynBehandling.InnsynResultatType.AVVIST, innsynDokumentList);
        var behandling = opprettBehandling(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD, innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.ENGANGSTØNAD);
        var dokumentHendelse = lagDokumentHendelse();

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynDokumentdata.InnsynResultatType.AVVIST);
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD.getKode());
        assertThat(innsynsDokumentData.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
    }

    private BrevGrunnlagDto opprettBehandling(BrevGrunnlagDto.FagsakYtelseType ytelseType, InnsynBehandling innsynsBehandling) {
        return DatamapperTestUtil.defaultBuilder()
            .behandlingType(BrevGrunnlagDto.BehandlingType.INNSYN)
            .innsynBehandling(innsynsBehandling)
            .fagsakYtelseType(ytelseType)
            .behandlingsresultat(BrevGrunnlagBuilders.behandlingsresultat().build())
            .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return lagStandardHendelseBuilder().medFritekst(FRITEKST).build();
    }
}

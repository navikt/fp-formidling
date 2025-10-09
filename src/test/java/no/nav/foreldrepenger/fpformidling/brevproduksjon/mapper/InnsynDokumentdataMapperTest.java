package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.InnsynBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.InnsynBehandling.InnsynDokument;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;

class InnsynDokumentdataMapperTest {

    private static final String FRITEKST = "Årsak til avvist er at det ikke mulig\ndet er fordi vi ikke har nok informasjon";

    private InnsynDokumentdataMapper innsynDokumentdataMapper;

    private List<InnsynDokument> innsynDokumentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        var dokument1 = new InnsynDokument(true, "124", "1");
        var dokument2 = new InnsynDokument(true, "125", "2");
        innsynDokumentList = List.of(dokument1, dokument2);

        innsynDokumentdataMapper = new InnsynDokumentdataMapper(brevParametere);
    }

    @Test
    void mappingAvInnsynInnvilgetForeldrePenger() {
        var innsynsBehandling = new InnsynBehandling(InnsynBehandling.InnsynResultatType.INNVILGET, innsynDokumentList);
        var behandling = opprettBehandling(BrevGrunnlag.FagsakYtelseType.FORELDREPENGER, innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.INNVILGET.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(innsynsDokumentData.getKlagefrist()).isEqualTo(6);
    }

    @Test
    void mappingAvInnsynAvvistEngangsstønadMedFritekst() {
        var innsynsBehandling = new InnsynBehandling(InnsynBehandling.InnsynResultatType.AVVIST, innsynDokumentList);
        var behandling = opprettBehandling(BrevGrunnlag.FagsakYtelseType.ENGANGSTØNAD, innsynsBehandling);

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.ENGANGSTØNAD);
        var dokumentHendelse = lagDokumentHendelse();

        var innsynsDokumentData = innsynDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(innsynsDokumentData.getInnsynResultat()).isEqualTo(InnsynResultatType.AVVIST.getKode());
        assertThat(innsynsDokumentData.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.ENGANGSTØNAD.getKode());
        assertThat(innsynsDokumentData.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
    }

    private BrevGrunnlag opprettBehandling(BrevGrunnlag.FagsakYtelseType ytelseType, InnsynBehandling innsynsBehandling) {
        return DatamapperTestUtil.innsynBrevGrunnlag(innsynsBehandling, ytelseType);
    }

    private DokumentHendelse lagDokumentHendelse() {
        return lagStandardHendelseBuilder().medFritekst(FRITEKST).build();
    }
}

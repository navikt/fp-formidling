package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.FagType;

public class InnsynskravSvarBrevMapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private DokumentHendelse.Builder dokumentHendelseBuilder;
    private InnsynskravSvarBrevMapper innsynskravSvarBrevMapper;
    private BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();

    @Mock
    private Innsyn innsyn;

    @Before
    public void setup() {
        innsynskravSvarBrevMapper = new InnsynskravSvarBrevMapper(brevParametere, null);
        dokumentHendelseBuilder = DokumentHendelse.builder()
                .medBestillingUuid(UUID.randomUUID())
                .medBehandlingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    @Test
    public void skal_mappe_innvilget_innsyn() {
        String fritekst = "FRITEKST";
        DokumentHendelse dokumentHendelse = dokumentHendelseBuilder
                .medFritekst(fritekst)
                .build();
        doReturn(InnsynResultatType.INNVILGET).when(innsyn).getInnsynResultatType();
        FagType fagtype = innsynskravSvarBrevMapper.mapFagType(dokumentHendelse, innsyn);
        assertThat(fagtype.getFritekst()).isEqualTo(fritekst);
    }
}

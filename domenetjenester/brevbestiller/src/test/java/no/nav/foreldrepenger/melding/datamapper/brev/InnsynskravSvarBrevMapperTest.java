package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.math.BigInteger;

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
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.YtelseTypeKode;

public class InnsynskravSvarBrevMapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private InnsynskravSvarBrevMapper innsynskravSvarBrevMapper;
    private BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();

    @Mock
    private Innsyn innsyn;

    @Before
    public void setup() {
        innsynskravSvarBrevMapper = new InnsynskravSvarBrevMapper(brevParametere, null);
    }

    @Test
    public void skal_mappe_innvilget_innsyn() {
        doReturn(InnsynResultatType.INNVILGET).when(innsyn).getInnsynResultatType();
        FagType fagtype = innsynskravSvarBrevMapper.mapFagType(DatamapperTestUtil.standardDokumenthendelse(), innsyn);
        assertThat(fagtype.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagtype.getInnsynResultatType()).isEqualTo(InnsynResultatTypeKode.INNVILGET);
        assertThat(fagtype.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagtype.getKlageFristUker()).isEqualTo(BigInteger.valueOf(brevParametere.getKlagefristUkerInnsyn()));
    }

    @Test
    public void skal_mappe_avslått_innsyn_es() {
        DokumentHendelse dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder()
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .build();
        doReturn(InnsynResultatType.AVVIST).when(innsyn).getInnsynResultatType();
        FagType fagtype = innsynskravSvarBrevMapper.mapFagType(dokumentHendelse, innsyn);
        assertThat(fagtype.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagtype.getInnsynResultatType()).isEqualTo(InnsynResultatTypeKode.AVVIST);
        assertThat(fagtype.getYtelseType()).isEqualTo(YtelseTypeKode.ES);
        assertThat(fagtype.getKlageFristUker()).isEqualTo(BigInteger.valueOf(brevParametere.getKlagefristUkerInnsyn()));
    }
}

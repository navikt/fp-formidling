package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.YtelseTypeKode;

@ExtendWith(MockitoExtension.class)
public class InnsynskravSvarBrevMapperTest {

    private InnsynskravSvarBrevMapper innsynskravSvarBrevMapper;
    private final BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();

    @Mock
    private Innsyn innsyn;

    @BeforeEach
    public void setup() {
        innsynskravSvarBrevMapper = new InnsynskravSvarBrevMapper(brevParametere, null);
    }

    @Test
    public void skal_mappe_innvilget_innsyn() {
        when(innsyn.getInnsynResultatType()).thenReturn(InnsynResultatType.INNVILGET);
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
        when(innsyn.getInnsynResultatType()).thenReturn(InnsynResultatType.AVVIST);
        FagType fagtype = innsynskravSvarBrevMapper.mapFagType(dokumentHendelse, innsyn);
        assertThat(fagtype.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagtype.getInnsynResultatType()).isEqualTo(InnsynResultatTypeKode.AVVIST);
        assertThat(fagtype.getYtelseType()).isEqualTo(YtelseTypeKode.ES);
        assertThat(fagtype.getKlageFristUker()).isEqualTo(BigInteger.valueOf(brevParametere.getKlagefristUkerInnsyn()));
    }
}

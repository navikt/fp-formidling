package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.PersonstatusKode;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

public class InnhentOpplysningerBrevMapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private InnhentOpplysningerBrevMapper brevMapper;
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    @Mock
    private Behandling behandling;

    private List<MottattDokument> mottatteDokumenter = new ArrayList<>();
    private Optional<KlageDokument> klageDokument = Optional.empty();

    @Before
    public void setup() {
        brevMapper = new InnhentOpplysningerBrevMapper(DatamapperTestUtil.getBrevParametere(), null);
        doReturn(BehandlingType.FØRSTEGANGSSØKNAD).when(behandling).getBehandlingType();
    }

    @Test
    public void skal_bruke_klage_til_å_velge_mottatt_dato() {
        KlageDokument mockKlagedokument = Mockito.mock(KlageDokument.class);
        klageDokument = Optional.of(mockKlagedokument);
        LocalDate førsteJanuar = LocalDate.of(2019, 1, 1);
        doReturn(førsteJanuar).when(mockKlagedokument).getMottattDato();
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(førsteJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);


    }


}

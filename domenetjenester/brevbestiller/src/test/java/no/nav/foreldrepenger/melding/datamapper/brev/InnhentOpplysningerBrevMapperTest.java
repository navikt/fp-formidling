package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FØRSTE_JANUAR_TJUENITTEN;
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
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.YtelseTypeKode;
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
        doReturn(FØRSTE_JANUAR_TJUENITTEN).when(mockKlagedokument).getMottattDato();
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(FØRSTE_JANUAR_TJUENITTEN));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_velge_vanlig_søknad__mottatt_dato() {
        LocalDate andreJanuar = LocalDate.of(2019, 1, 2);
        mottatteDokumenter.add(new MottattDokument(andreJanuar, DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL, DokumentKategori.SØKNAD));
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(andreJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_velge_mottatt_dato_fra_endringssøknad_når_endring() {
        doReturn(List.of(opprettBehandlingsårsak(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER))).when(behandling).getBehandlingÅrsaker();
        LocalDate andreJanuar = LocalDate.of(2019, 1, 2);
        mottatteDokumenter.add(new MottattDokument(andreJanuar, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(andreJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.ENDRINGSSØKNAD);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_fungere_med_svp() {
        DokumentHendelse dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER).build();
        FagType fagType = brevMapper.mapFagType(dokumentFelles, dokumentHendelse, behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.SVP);
    }

    private BehandlingÅrsak opprettBehandlingsårsak(BehandlingÅrsakType årsakType) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(årsakType)
                .build();
    }

}

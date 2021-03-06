package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FØRSTE_JANUAR_TJUENITTEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
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
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

@ExtendWith(MockitoExtension.class)
public class InnhentOpplysningerBrevMapperTest {

    private InnhentOpplysningerBrevMapper brevMapper;
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    @Mock
    private Behandling behandling;

    private List<MottattDokument> mottatteDokumenter = new ArrayList<>();
    private Optional<KlageDokument> klageDokument = Optional.empty();

    @BeforeEach
    public void setup() {
        brevMapper = new InnhentOpplysningerBrevMapper(new BrevMapperUtil(DatamapperTestUtil.getBrevParametere()), null);
        lenient().when(behandling.getBehandlingType()).thenReturn(BehandlingType.FØRSTEGANGSSØKNAD);
    }

    @Test
    public void skal_bruke_klage_til_å_velge_mottatt_dato() {
        klageDokument = Optional.of(new KlageDokument(FØRSTE_JANUAR_TJUENITTEN));
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter,
                klageDokument);
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
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter,
                klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(andreJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_velge_mottattt_dato_fra_siste_søknad_mottatt_når_flere() {
        LocalDate andreJanuar = LocalDate.of(2019, 1, 2);
        LocalDate fjerdeJanuar = LocalDate.of(2019, 1, 4);
        LocalDate forsteJanuar = LocalDate.of(2019, 1, 1);
        ;
        mottatteDokumenter.add(new MottattDokument(andreJanuar, DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL, DokumentKategori.SØKNAD));
        mottatteDokumenter.add(new MottattDokument(fjerdeJanuar, DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL, DokumentKategori.SØKNAD));
        mottatteDokumenter.add(new MottattDokument(forsteJanuar, DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL, DokumentKategori.SØKNAD));
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter,
                klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(fjerdeJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_velge_siste_mottatt_dato_fra_endringssøknad_når_endring() {
        when(behandling.erRevurdering()).thenReturn(Boolean.TRUE);
        when(behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER)).thenReturn(Boolean.TRUE);
        LocalDate andreJanuar = LocalDate.of(2019, 1, 2);
        LocalDate fjerdeJanuar = LocalDate.of(2019, 1, 4);
        mottatteDokumenter.add(new MottattDokument(andreJanuar, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
        mottatteDokumenter.add(new MottattDokument(fjerdeJanuar, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
        FagType fagType = brevMapper.mapFagType(dokumentFelles, DatamapperTestUtil.standardDokumenthendelse(), behandling, mottatteDokumenter,
                klageDokument);
        assertThat(fagType.getSoknadDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(fjerdeJanuar));
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.ENDRINGSSØKNAD);
        assertThat(fagType.getFritekst()).isEqualTo(FRITEKST);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
    }

    @Test
    public void skal_fungere_med_svp() {
        DokumentHendelse dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER)
                .build();
        FagType fagType = brevMapper.mapFagType(dokumentFelles, dokumentHendelse, behandling, mottatteDokumenter, klageDokument);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.SVP);
    }

}

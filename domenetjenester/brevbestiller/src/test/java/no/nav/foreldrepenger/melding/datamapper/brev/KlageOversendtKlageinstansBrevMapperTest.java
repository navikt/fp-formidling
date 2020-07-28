package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SVARFRIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class KlageOversendtKlageinstansBrevMapperTest {

    private static final String FRITEKST = "FRITEKST";
    private static final String KLAGE_FRITEKST = "KLAGE FRITEKST";
    private static final LocalDate FRIST_DATO = LocalDate.now().plusDays(SVARFRIST.getDays());
    private static final LocalDate MOTTATT_DATO = LocalDate.now();
    private static final int BEHANDLINGSFRIST_UKER_KA = 14;

    private Klage klage = Mockito.mock(Klage.class);
    private DomeneobjektProvider domeneobjektProvider = Mockito.mock(DomeneobjektProvider.class);
    private KlageDokument klagedokument = Mockito.mock(KlageDokument.class);

    private FellesType fellesType = DatamapperTestUtil.getFellesType();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();
    private KlageOversendtKlageinstansBrevMapper brevMapper = new KlageOversendtKlageinstansBrevMapper(brevParametere, domeneobjektProvider);

    @Before
    public void setup() {
        when(domeneobjektProvider.hentKlagebehandling(any())).thenReturn(klage);
        when(domeneobjektProvider.hentKlageDokument(any())).thenReturn(klagedokument);
        when(klagedokument.getMottattDato()).thenReturn(MOTTATT_DATO);
    }

    @Test
    public void skal_mappe_oversendt_klage_for_SVP_uten_fritekst() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, null);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny().medFritekstTilbrev(KLAGE_FRITEKST).build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<mottattDato>%s</mottattDato>", MOTTATT_DATO));
        assertThat(xml).containsOnlyOnce(String.format("<antallUker>%d</antallUker>", BEHANDLINGSFRIST_UKER_KA));
        assertThat(xml).containsOnlyOnce(String.format("<fritekst>%s</fritekst>", KLAGE_FRITEKST));
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_oversendt_klage_for_SVP() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<mottattDato>%s</mottattDato>", MOTTATT_DATO));
        assertThat(xml).containsOnlyOnce(String.format("<antallUker>%d</antallUker>", BEHANDLINGSFRIST_UKER_KA));
        assertThat(xml).containsOnlyOnce(String.format("<fritekst>%s</fritekst>", FRITEKST));
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_oversendt_klage_for_ES() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, FRITEKST);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<mottattDato>%s</mottattDato>", MOTTATT_DATO));
        assertThat(xml).containsOnlyOnce(String.format("<antallUker>%d</antallUker>", BEHANDLINGSFRIST_UKER_KA));
        assertThat(xml).containsOnlyOnce(String.format("<fritekst>%s</fritekst>", FRITEKST));
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_oversendt_klage_for_FP() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER, FRITEKST);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.FORELDREPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<mottattDato>%s</mottattDato>", MOTTATT_DATO));
        assertThat(xml).containsOnlyOnce(String.format("<antallUker>%d</antallUker>", BEHANDLINGSFRIST_UKER_KA));
        assertThat(xml).containsOnlyOnce(String.format("<fritekst>%s</fritekst>", FRITEKST));
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_oversendt_klage_for_FP_uten_mottatt_dato_på_klagedokument() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER, FRITEKST);
        LocalDateTime opprettet_dato = LocalDateTime.now();
        Behandling behandling = DatamapperTestUtil.standardBehandlingBuilder()
                .medOpprettetDato(opprettet_dato)
                .build();
        when(klagedokument.getMottattDato()).thenReturn(null);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.FORELDREPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<mottattDato>%s</mottattDato>", opprettet_dato.toLocalDate()));
        assertThat(xml).containsOnlyOnce(String.format("<antallUker>%d</antallUker>", BEHANDLINGSFRIST_UKER_KA));
        assertThat(xml).containsOnlyOnce(String.format("<fritekst>%s</fritekst>", FRITEKST));
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    private DokumentHendelse byggHendelse(FagsakYtelseType ytelseType, String fritekst) {
        return DokumentHendelse.builder()
                .medYtelseType(ytelseType)
                .medFritekst(fritekst)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .build();
    }
}

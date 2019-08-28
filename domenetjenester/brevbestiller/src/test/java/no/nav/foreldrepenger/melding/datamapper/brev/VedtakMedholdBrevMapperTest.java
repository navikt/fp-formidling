package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class VedtakMedholdBrevMapperTest {

    private static final String FRITEKST = "FRITEKST";
    private static final String KLAGE_FRITEKST = "KLAGE FRITEKST";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    private Klage klage = Mockito.mock(Klage.class);
    private DomeneobjektProvider domeneobjektProvider = Mockito.mock(DomeneobjektProvider.class);

    private DokumentRepository dokumentRepository = new DokumentRepositoryImpl(repoRule.getEntityManager());
    private VedtakMedholdBrevMapper brevMapper = new VedtakMedholdBrevMapper(domeneobjektProvider, DatamapperTestUtil.getBrevParametere());
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private FellesType fellesType = DatamapperTestUtil.getFellesType();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private KlageFormkravResultat klageFormkravResultat = KlageFormkravResultat.ny().build();
    private KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny().medFritekstTilbrev(KLAGE_FRITEKST).build();

    @Before
    public void setup() {
        doReturn(klage).when(domeneobjektProvider).hentKlagebehandling(any());
    }

    @Test
    public void skal_mappe_vedtak_medhold_brev_for_SVP_og_FAMPEN() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).contains("<ytelseType>SVP</ytelseType>");
        assertThat(xml).contains("<opphavType>FAMPEN</opphavType>");
        assertThat(xml).contains("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).contains("<klageFristUker>14</klageFristUker>");
    }

    @Test
    public void skal_mappe_vedtak_medhold_brev_for_SVP_og_KLAGE() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST);
        doReturn(klageFormkravResultat).when(klage).getFormkravKA();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).contains("<ytelseType>SVP</ytelseType>");
        assertThat(xml).contains("<opphavType>KLAGE</opphavType>");
        assertThat(xml).contains("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).contains("<klageFristUker>14</klageFristUker>");
    }

    @Test
    public void skal_mappe_vedtak_medhold_brev_for_FP_og_KLAGE() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER, FRITEKST);
        doReturn(klageFormkravResultat).when(klage).getFormkravKA();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).contains("<ytelseType>FP</ytelseType>");
        assertThat(xml).contains("<opphavType>KLAGE</opphavType>");
        assertThat(xml).contains("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).contains("<klageFristUker>14</klageFristUker>");
    }

    @Test
    public void skal_mappe_vedtak_medhold_brev_for_ES_og_KLAGE_men_bruk_fritekst_fra_gjeldende_klage() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, null);
        doReturn(klageFormkravResultat).when(klage).getFormkravKA();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).contains("<ytelseType>ES</ytelseType>");
        assertThat(xml).contains("<opphavType>KLAGE</opphavType>");
        assertThat(xml).contains("<fritekst>KLAGE FRITEKST</fritekst>");
        assertThat(xml).contains("<klageFristUker>14</klageFristUker>");
    }

    private DokumentHendelse byggHendelse(FagsakYtelseType ytelseType, String fritekst) {
        return DokumentHendelse.builder()
                .medYtelseType(ytelseType)
                .medFritekst(fritekst)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(DokumentMalType.VEDTAK_MEDHOLD))
                .build();
    }

}

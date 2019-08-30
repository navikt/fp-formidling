package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SVARFRIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;
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
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.vedtak.util.FPDateUtil;

public class KlageYtelsesvedtakOpphevetBrevMapperTest {

    private static final String FRITEKST = "FRITEKST";
    private static final String KLAGE_FRITEKST = "KLAGE FRITEKST";
    private static final LocalDate FRIST_DATO = FPDateUtil.iDag().plusDays(SVARFRIST.getDays());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    private Klage klage = Mockito.mock(Klage.class);
    private DomeneobjektProvider domeneobjektProvider = Mockito.mock(DomeneobjektProvider.class);

    private FellesType fellesType = DatamapperTestUtil.getFellesType();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();
    private KlageYtelsesvedtakOpphevetBrevMapper brevMapper = new KlageYtelsesvedtakOpphevetBrevMapper(brevParametere, domeneobjektProvider);

    @Before
    public void setup() {
        doReturn(klage).when(domeneobjektProvider).hentKlagebehandling(any());
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_SVP_med_opphevet_klage() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST, true);
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>SVP</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>true</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_SVP_uten_opphevet_klage_og_med_klage_vurdering_OPPHEVE_YTELSESVEDTAK() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST, false);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medKlageVurdering(KlageVurdering.OPPHEVE_YTELSESVEDTAK)
                .medFritekstTilbrev(KLAGE_FRITEKST)
                .build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>SVP</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>true</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_SVP_uten_opphevet_klage_og_uten_klage_vurdering_OPPHEVE_YTELSESVEDTAK() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, FRITEKST, false);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medKlageVurdering(KlageVurdering.AVVIS_KLAGE)
                .medFritekstTilbrev(KLAGE_FRITEKST)
                .build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>SVP</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>false</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_SVP_med_KLAGE_FRITEKST() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, null, true);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev(KLAGE_FRITEKST)
                .build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>SVP</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>true</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>KLAGE FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_FP() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER, FRITEKST, true);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny().build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>FP</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>true</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    @Test
    public void skal_mappe_vedtak_opphevet_for_ES() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, FRITEKST, true);
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny().build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        // Act
        String xml = brevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce("<ytelseType>ES</ytelseType>");
        assertThat(xml).containsOnlyOnce("<opphevet>true</opphevet>");
        assertThat(xml).containsOnlyOnce("<antallUker>4</antallUker>");
        assertThat(xml).containsOnlyOnce("<fritekst>FRITEKST</fritekst>");
        assertThat(xml).containsOnlyOnce(String.format("<fristDato>%s</fristDato>", FRIST_DATO));
    }

    private DokumentHendelse byggHendelse(FagsakYtelseType ytelseType, String fritekst, boolean erOpphevetKlage) {
        return DokumentHendelse.builder()
                .medYtelseType(ytelseType)
                .medFritekst(fritekst)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medErOpphevetKlage(erOpphevetKlage)
                .build();
    }

}

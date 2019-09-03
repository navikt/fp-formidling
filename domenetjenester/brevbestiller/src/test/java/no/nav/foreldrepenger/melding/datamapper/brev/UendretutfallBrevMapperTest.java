package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public class UendretutfallBrevMapperTest {

    private UendretutfallBrevMapper mapper = new UendretutfallBrevMapper();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private FellesType fellesType = DatamapperTestUtil.getFellesType();

    @Test
    public void skal_mappe_korrekt_for_FP() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.FORELDREPENGER.getKode()));
    }

    @Test
    public void skal_mappe_korrekt_for_SVP() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
    }

    @Test
    public void skal_mappe_korrekt_for_ES() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
    }

    private DokumentHendelse byggHendelse(FagsakYtelseType ytelseType) {
        return DokumentHendelse.builder()
                .medYtelseType(ytelseType)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .build();
    }

}

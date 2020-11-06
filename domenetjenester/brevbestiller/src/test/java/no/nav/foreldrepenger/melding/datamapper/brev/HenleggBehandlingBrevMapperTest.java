package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public class HenleggBehandlingBrevMapperTest {

    private static final String BEHANDLENDE_ENHET_NAVN_FAMPEN = "Enheten på Saturn, NAV Familie- og pensjonsytelser";
    private static final String BEHANDLENDE_ENHET_NAVN_KLAGE = "Enheten på Saturn, Klage";

    private static final String FØRSTEGANGSSØKNAD = "FØRSTEGANGSSØKNAD";
    private static final String REVURDERING = "REVURDERING";
    private static final String KLAGE = "KLAGE";
    private static final String INNSYN = "INNSYN";

    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private FellesType fellesType = DatamapperTestUtil.getFellesType();
    private HenleggBehandlingBrevMapper mapper = new HenleggBehandlingBrevMapper();

    @Test
    public void skal_mappe_SVP_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_FAMPEN_fra_hendelsen() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, BEHANDLENDE_ENHET_NAVN_FAMPEN);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, null);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.FAMPEN.value()));
    }

    @Test
    public void skal_mappe_SVP_som_REVURDERING_med_BEHANDLENDE_ENHET_NAVN_FAMPEN_fra_hendelsen() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, BEHANDLENDE_ENHET_NAVN_FAMPEN);
        Behandling behandling = byggBehandling(BehandlingType.REVURDERING, null);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", REVURDERING));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.FAMPEN.value()));
    }

    @Test
    public void skal_mappe_SVP_som_KLAGE_med_BEHANDLENDE_ENHET_NAVN_FAMPEN_fra_hendelsen() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, BEHANDLENDE_ENHET_NAVN_FAMPEN);
        Behandling behandling = byggBehandling(BehandlingType.KLAGE, null);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", KLAGE));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.FAMPEN.value()));
    }

    @Test
    public void skal_mappe_SVP_som_INNSYN_med_BEHANDLENDE_ENHET_NAVN_FAMPEN_fra_hendelsen() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, BEHANDLENDE_ENHET_NAVN_FAMPEN);
        Behandling behandling = byggBehandling(BehandlingType.INNSYN, null);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", INNSYN));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.FAMPEN.value()));
    }

    @Test
    public void skal_mappe_SVP_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_hendelsen() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, BEHANDLENDE_ENHET_NAVN_KLAGE);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, null);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.KLAGE.value()));
    }

    @Test
    public void skal_mappe_SVP_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_behandling() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.SVANGERSKAPSPENGER, null);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BEHANDLENDE_ENHET_NAVN_KLAGE);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.KLAGE.value()));
    }

    @Test
    public void skal_mappe_FP_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_behandling() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.FORELDREPENGER, null);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BEHANDLENDE_ENHET_NAVN_KLAGE);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.FORELDREPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.KLAGE.value()));
    }

    @Test
    public void skal_mappe_ES_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_behandling() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, null);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BEHANDLENDE_ENHET_NAVN_KLAGE);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.KLAGE.value()));
    }

    @Test
    public void skal_mappe_ES_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_behandling_men_hendelse_har_tom_string()
            throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, "");
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BEHANDLENDE_ENHET_NAVN_KLAGE);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.KLAGE.value()));
    }

    @Test
    public void skal_mappe_ES_som_FØRSTEGANGSSØKNAD_med_BEHANDLENDE_ENHET_NAVN_KLAGE_fra_behandling_og_BEHANDLENDE_ENHET_NAVN_FAMPEN_fra_hendelsen()
            throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        DokumentHendelse dokumentHendelse = byggHendelse(FagsakYtelseType.ENGANGSTØNAD, BEHANDLENDE_ENHET_NAVN_FAMPEN);
        Behandling behandling = byggBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BEHANDLENDE_ENHET_NAVN_KLAGE);
        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsType>%s</behandlingsType>", FØRSTEGANGSSØKNAD));
        assertThat(xml).containsOnlyOnce(String.format("<opphavType>%s</opphavType>", OpphavTypeKode.FAMPEN.value()));
    }

    private Behandling byggBehandling(BehandlingType behandlingType, String behandlendeEnhetNavn) {
        return Behandling.builder()
                .medId(123L)
                .medBehandlingType(behandlingType)
                .medBehandlendeEnhetNavn(behandlendeEnhetNavn)
                .build();
    }

    private DokumentHendelse byggHendelse(FagsakYtelseType ytelseType, String behandlendeEnhetNavn) {
        return DokumentHendelse.builder()
                .medYtelseType(ytelseType)
                .medBehandlendeEnhetNavn(behandlendeEnhetNavn)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .build();
    }

}

package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SOEKERS_NAVN;
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
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.VariantKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

public class ForlengetSaksbehandlingstidBrevMapperTest {


    private ForlengetSaksbehandlingstidBrevMapper forlengetSaksbehandlingstidBrevMapper = new ForlengetSaksbehandlingstidBrevMapper();
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private FellesType fellesType = DatamapperTestUtil.getFellesType();

    @Test
    public void skal_mappe_forlengelse_brev_for_FP_med_FORLENGET_OPPTJENING() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_OPPTJENING, FagsakYtelseType.FORELDREPENGER);
        // Act
        String xml = forlengetSaksbehandlingstidBrevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.FORELDREPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<variant>%s</variant>", VariantKode.OPPTJENING.value()));
        assertThat(xml).containsOnlyOnce(String.format("<personstatus>%s</personstatus>", PersonstatusKode.ANNET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsfristUker>%s</behandlingsfristUker>", BehandlingType.FØRSTEGANGSSØKNAD.getBehandlingstidFristUker()));
        assertThat(xml).containsOnlyOnce(String.format("<sokersNavn>%s</sokersNavn>", SOEKERS_NAVN));
    }

    @Test
    public void skal_mappe_forlengelse_brev_for_ES_med_FORLENGET_DOK() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_DOK, FagsakYtelseType.ENGANGSTØNAD);
        // Act
        String xml = forlengetSaksbehandlingstidBrevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.ENGANGSTØNAD.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<variant>%s</variant>", VariantKode.FORLENGET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<personstatus>%s</personstatus>", PersonstatusKode.ANNET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsfristUker>%s</behandlingsfristUker>", BehandlingType.FØRSTEGANGSSØKNAD.getBehandlingstidFristUker()));
        assertThat(xml).containsOnlyOnce(String.format("<sokersNavn>%s</sokersNavn>", SOEKERS_NAVN));
    }

    @Test
    public void skal_mappe_forlengelse_brev_for_SVP_med_FORLENGET_TIDLIG_SOK() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_TIDLIG_SOK, FagsakYtelseType.SVANGERSKAPSPENGER);
        // Act
        String xml = forlengetSaksbehandlingstidBrevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<variant>%s</variant>", VariantKode.FORTIDLIG.value()));
        assertThat(xml).containsOnlyOnce(String.format("<personstatus>%s</personstatus>", PersonstatusKode.ANNET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsfristUker>%s</behandlingsfristUker>", BehandlingType.FØRSTEGANGSSØKNAD.getBehandlingstidFristUker()));
        assertThat(xml).containsOnlyOnce(String.format("<sokersNavn>%s</sokersNavn>", SOEKERS_NAVN));
    }

    @Test
    public void skal_mappe_forlengelse_brev_for_SVP_med_FORLENGET_MEDL_DOK() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_MEDL_DOK, FagsakYtelseType.SVANGERSKAPSPENGER);
        // Act
        String xml = forlengetSaksbehandlingstidBrevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<variant>%s</variant>", VariantKode.MEDLEM.value()));
        assertThat(xml).containsOnlyOnce(String.format("<personstatus>%s</personstatus>", PersonstatusKode.ANNET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsfristUker>%s</behandlingsfristUker>", BehandlingType.FØRSTEGANGSSØKNAD.getBehandlingstidFristUker()));
        assertThat(xml).containsOnlyOnce(String.format("<sokersNavn>%s</sokersNavn>", SOEKERS_NAVN));
    }

    @Test
    public void skal_mappe_forlengelse_brev_for_SVP_med_klagebehandling() throws JAXBException, SAXException, XMLStreamException {
        // Arrange
        Behandling behandling = Behandling.builder().medBehandlingType(BehandlingType.KLAGE).build();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.KLAGE_OMGJØRING, FagsakYtelseType.SVANGERSKAPSPENGER);
        // Act
        String xml = forlengetSaksbehandlingstidBrevMapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);
        // Assert
        assertThat(xml).containsOnlyOnce(String.format("<ytelseType>%s</ytelseType>", FagsakYtelseType.SVANGERSKAPSPENGER.getKode()));
        assertThat(xml).containsOnlyOnce(String.format("<variant>%s</variant>", VariantKode.KLAGE.value()));
        assertThat(xml).containsOnlyOnce(String.format("<personstatus>%s</personstatus>", PersonstatusKode.ANNET.value()));
        assertThat(xml).containsOnlyOnce(String.format("<behandlingsfristUker>%s</behandlingsfristUker>", BehandlingType.KLAGE.getBehandlingstidFristUker()));
        assertThat(xml).containsOnlyOnce(String.format("<sokersNavn>%s</sokersNavn>", SOEKERS_NAVN));
    }

    private DokumentHendelse byggHendelse(DokumentMalType mal, FagsakYtelseType ytelseType) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(ytelseType)
                .medDokumentMalType(mal)
                .build();
    }

}

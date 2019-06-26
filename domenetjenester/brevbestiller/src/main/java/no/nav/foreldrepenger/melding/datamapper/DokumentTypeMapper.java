package no.nav.foreldrepenger.melding.datamapper;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public abstract class DokumentTypeMapper implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentTypeMapper.class);

    protected DomeneobjektProvider domeneobjektProvider = null;

    protected DokumentTypeMapper() {
    }

    public abstract String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException;

    @Override
    public void close() throws Exception {
        if (domeneobjektProvider != null && domeneobjektProvider.getJsonTestdata() != null) {
            LOG.debug(domeneobjektProvider.getJsonTestdata());
        }
    }
}


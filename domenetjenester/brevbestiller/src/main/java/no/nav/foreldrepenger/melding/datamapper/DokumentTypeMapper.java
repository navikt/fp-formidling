package no.nav.foreldrepenger.melding.datamapper;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;

public interface DokumentTypeMapper {

    String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelseDto dokumentHendelseDto) throws JAXBException, SAXException, XMLStreamException;

}


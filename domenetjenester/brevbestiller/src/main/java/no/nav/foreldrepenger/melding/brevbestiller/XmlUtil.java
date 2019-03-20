package no.nav.foreldrepenger.melding.brevbestiller;

import java.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.vedtak.feil.FeilFactory;

public class XmlUtil {
    private XmlUtil() {

    }

    public static String elementTilString(Element element) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) element.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
        return serializer.writeToString(element);
    }

    public static XMLGregorianCalendar finnDatoVerdiAvUtenTidSone(LocalDate dato) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(dato.getYear(), dato.getMonthValue(), dato.getDayOfMonth(), -2147483648, -2147483648, -2147483648, -2147483648, -2147483648);
        } catch (DatatypeConfigurationException e) {
            throw FeilFactory.create(DokumentBestillerFeil.class).datokonverteringsfeil(dato.toString(), e).toException();

        }
    }
}

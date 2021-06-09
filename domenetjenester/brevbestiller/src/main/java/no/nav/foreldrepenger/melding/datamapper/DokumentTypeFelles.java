package no.nav.foreldrepenger.melding.datamapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.vedtak.exception.TekniskException;

public class DokumentTypeFelles {

    public static XMLGregorianCalendar finnDatoVerdiAvUtenTidSone(String datoString) {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dato = dateFormat.parse(datoString);
            LocalDate localDate = dato.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), -2147483648, -2147483648, -2147483648, -2147483648, -2147483648);
        } catch (ParseException | DatatypeConfigurationException e) {
            throw new TekniskException("FPFORMIDLING-151337", String.format("Kan ikke konvertere dato %s til xmlformatert dato.", datoString), e);
        }
    }
}

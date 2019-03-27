package no.nav.foreldrepenger.melding.datamapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeData;
import no.nav.vedtak.feil.FeilFactory;

public class DokumentTypeFelles {

    public static Optional<String> finnOptionalVerdiAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        Optional<DokumentTypeData> felt = dokumentTypeDataListe.stream()
                .filter(dtd -> feltnavn.equalsIgnoreCase(dtd.getDoksysId()))
                .findFirst();
        return felt.isPresent() && felt.get().getVerdi() != null ? Optional.of(felt.get().getVerdi()) : Optional.empty();
    }

    public static String finnVerdiAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        Optional<String> res = finnOptionalVerdiAv(feltnavn, dokumentTypeDataListe);
        if (!res.isPresent()) {
            throw FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi(feltnavn).toException();
        }
        return res.get();
    }

    public static Optional<String> finnOptionalStrukturertVerdiAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        Optional<DokumentTypeData> felt = dokumentTypeDataListe.stream()
                .filter(dtd -> feltnavn.equalsIgnoreCase(dtd.getDoksysId()))
                .findFirst();
        return felt.isPresent() && felt.get().getStrukturertVerdi() != null ? Optional.of(felt.get().getStrukturertVerdi()) : Optional.empty();
    }

    public static String finnStrukturertVerdiAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        Optional<String> res = finnOptionalStrukturertVerdiAv(feltnavn, dokumentTypeDataListe);
        if (!res.isPresent()) {
            throw FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi(feltnavn).toException();
        }
        return res.get();
    }

    public static List<DokumentTypeData> finnListeMedVerdierAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        return dokumentTypeDataListe.stream()
                .filter(dtd -> feltnavn.equalsIgnoreCase(dtd.getDoksysId().split(":")[0]))
                .collect(Collectors.toList());
    }

    public static XMLGregorianCalendar finnDatoVerdiAv(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        String datoString = finnVerdiAv(feltnavn, dokumentTypeDataListe);
        return tilXMLformat(datoString);
    }

    public static Optional<XMLGregorianCalendar> finnOptionalDatoVerdiAvUtenTidSone(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        Optional<String> optionalDatoString = finnOptionalVerdiAv(feltnavn, dokumentTypeDataListe);
        return optionalDatoString.map(DokumentTypeFelles::finnDatoVerdiAvUtenTidSone);
    }

    public static XMLGregorianCalendar finnDatoVerdiAvUtenTidSone(String datoString) {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dato = dateFormat.parse(datoString);
            LocalDate localDate = dato.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), -2147483648, -2147483648, -2147483648, -2147483648, -2147483648);
        } catch (ParseException | DatatypeConfigurationException e) {
            throw FeilFactory.create(DokumentBestillerFeil.class).datokonverteringsfeil(datoString, e).toException();
        }
    }

    public static XMLGregorianCalendar finnDatoVerdiAvUtenTidSone(String feltnavn, List<DokumentTypeData> dokumentTypeDataListe) {
        return finnDatoVerdiAvUtenTidSone(finnVerdiAv(feltnavn, dokumentTypeDataListe));
    }

    private static XMLGregorianCalendar tilXMLformat(String datoString) {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dato = dateFormat.parse(datoString);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(dato);
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        } catch (ParseException | DatatypeConfigurationException e) {
            throw FeilFactory.create(DokumentBestillerFeil.class).datokonverteringsfeil(datoString, e).toException();
        }
    }

    public static String fjernNamespaceFra(String xml) {
        return xml.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
                replaceAll(" xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
                .replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
                .replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
    }

    // For å få riktig formateing på teksten som ble lagret som JSON i databasen
    public static String formaterStrukturertVerdiEtterLagringSomJson(String strukturertVerdi) {
        return strukturertVerdi.replaceAll("(\\\\r)?\\\\n", "\n").replaceAll("^\"|\"$", "");
    }
}

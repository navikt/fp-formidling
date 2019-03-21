package no.nav.foreldrepenger.melding.datamapper;

public class DokumentTypeFelles {
    public static String fjernNamespaceFra(String xml) {
        return xml.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
                replaceAll(" xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
                .replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
                .replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
    }
}

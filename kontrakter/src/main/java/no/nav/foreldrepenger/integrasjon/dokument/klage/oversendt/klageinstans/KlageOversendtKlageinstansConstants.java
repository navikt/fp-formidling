package no.nav.foreldrepenger.integrasjon.dokument.klage.oversendt.klageinstans;

public final class KlageOversendtKlageinstansConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000060.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.integrasjon.dokument.klage.oversendt.klageinstans.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private KlageOversendtKlageinstansConstants() {
    }
}

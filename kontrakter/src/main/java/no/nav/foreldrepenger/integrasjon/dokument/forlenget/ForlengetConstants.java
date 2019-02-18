package no.nav.foreldrepenger.integrasjon.dokument.forlenget;

public final class ForlengetConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000056.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.integrasjon.dokument.forlenget.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private ForlengetConstants() {
    }
}

package no.nav.foreldrepenger.integrasjon.dokument.innsyn;

public final class InnsynConstants {

    public static final String XSD_LOCATION = "xsd/foreldrepenger_000071.xsd";

    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.integrasjon.dokument.innsyn.v1";

    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private InnsynConstants() {
    }
}

package no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev;

public final class FritekstbrevConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000096.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.melding.integrasjon.dokumentkontrakter.dokument.fritekstbrev.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private FritekstbrevConstants() {
    }
}

package no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist;

public final class KlageAvvistConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000054.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.melding.integrasjon.dokumentkontrakter.dokument.klage.avvist.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private KlageAvvistConstants() {
    }
}

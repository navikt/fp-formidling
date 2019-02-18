package no.nav.foreldrepenger.integrasjon.dokument.klage.ytelsesvedtak.opphevet;

public final class KlageYtelsesvedtakOpphevetConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000059.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.integrasjon.dokument.klage.ytelsesvedtak.opphevet.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private KlageYtelsesvedtakOpphevetConstants() {
    }
}

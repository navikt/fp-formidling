package no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger;

public final class InnhentopplysningerConstants {
    public static final String XSD_LOCATION = "xsd/foreldrepenger_000049.xsd";
    // Legger inn en fiktiv adresse da det ikke eksisterer en i xsden
    public static final String NAMESPACE = "urn:no.nav.foreldrepenger.melding.integrasjon.dokumentkontrakter.dokument.innhentopplysninger.v1";
    public static final Class<BrevdataType> JAXB_CLASS = BrevdataType.class;

    private InnhentopplysningerConstants() {
    }
}

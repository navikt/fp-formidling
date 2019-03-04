package no.nav.foreldrepenger.melding.kodeverk.arkiv;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "ArkivFilType")
@DiscriminatorValue(ArkivFilType.DISCRIMINATOR)
public class ArkivFilType extends Kodeliste {

    public static final String DISCRIMINATOR = "ARKIV_FILTYPE";

    public static final ArkivFilType PDF = new ArkivFilType("PDF");
    public static final ArkivFilType PDFA = new ArkivFilType("PDFA");
    public static final ArkivFilType XML = new ArkivFilType("XML");
    public static final ArkivFilType AFP = new ArkivFilType("AFP");
    public static final ArkivFilType AXML = new ArkivFilType("AXML");
    public static final ArkivFilType DLF = new ArkivFilType("DLF");
    public static final ArkivFilType DOC = new ArkivFilType("DOC");
    public static final ArkivFilType DOCX = new ArkivFilType("DOCX");
    public static final ArkivFilType JPEG = new ArkivFilType("JPEG");
    public static final ArkivFilType RTF = new ArkivFilType("RTF");
    public static final ArkivFilType TIFF = new ArkivFilType("TIFF");
    public static final ArkivFilType XLS = new ArkivFilType("XLS");
    public static final ArkivFilType XLSX = new ArkivFilType("XLSX");

    public static final ArkivFilType UDEFINERT = new ArkivFilType("-"); //$NON-NLS-1$


    public ArkivFilType() {
        // For Hibernate
    }

    public ArkivFilType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

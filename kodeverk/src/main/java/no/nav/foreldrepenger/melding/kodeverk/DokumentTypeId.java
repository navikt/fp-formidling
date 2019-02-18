package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * DokumentTypeId er et kodeverk som forvaltes av Kodeverkforvaltning. Det er et subsett av kodeverket DokumentType,  mer spesifikt alle inngående dokumenttyper.
 *
 * @see DokumentType
 */
@Entity(name = "DokumentTypeId")
@DiscriminatorValue(DokumentTypeId.DISCRIMINATOR)
public class DokumentTypeId extends Kodeliste {

    public static final String DISCRIMINATOR = "DOKUMENT_TYPE_ID";
    public static final DokumentTypeId INNTEKTSMELDING = new DokumentTypeId("INNTEKTSMELDING");

    public static final DokumentTypeId UDEFINERT = new DokumentTypeId("-"); //$NON-NLS-1$

    public DokumentTypeId() {
        // Hibernate trenger en
    }

    private DokumentTypeId(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

package no.nav.foreldrepenger.fpformidling.dokumentdata;

import java.util.Objects;
import java.util.Set;


/**
 * DokumentTypeId er et kodeverk som forvaltes av Kodeverkforvaltning. Det er et subsett av kodeverket DokumentType,  mer spesifikt alle inngående dokumenttyper.
 */
public class DokumentTypeId {

    public static final DokumentTypeId FORELDREPENGER_ENDRING_SØKNAD = new DokumentTypeId("I000050"); //$NON-NLS-1$
    public static final DokumentTypeId SØKNAD_SVANGERSKAPSPENGER = new DokumentTypeId("I000001"); //$NON-NLS-1$

    private static final Set<String> SØKNAD_KODER = Set.of("SØKNAD_SVANGERSKAPSPENGER", "I000001", "SØKNAD_FORELDREPENGER_ADOPSJON", "I000002",
            "SØKNAD_ENGANGSSTØNAD_FØDSEL", "I000003", "SØKNAD_ENGANGSSTØNAD_ADOPSJON", "I000004", "SØKNAD_FORELDREPENGER_FØDSEL", "I000005");
    private static final Set<String> ENDRING_KODER = Set.of("FLEKSIBELT_UTTAK_FORELDREPENGER", "I000006", "FORELDREPENGER_ENDRING_SØKNAD", "I000050");

    private String kode;

    public DokumentTypeId(String kode) {
        this.kode = kode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (DokumentTypeId) o;
        return Objects.equals(kode, that.kode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode);
    }

    public String getKode() {
        return kode;
    }

    public boolean erEndringsøknadType() {
        return ENDRING_KODER.contains(this.kode);
    }

    public boolean erSøknadType() {
        return SØKNAD_KODER.contains(this.kode);
    }
}

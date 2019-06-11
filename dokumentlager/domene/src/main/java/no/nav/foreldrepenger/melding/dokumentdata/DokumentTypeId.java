package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Objects;
import java.util.Set;


/**
 * DokumentTypeId er et kodeverk som forvaltes av Kodeverkforvaltning. Det er et subsett av kodeverket DokumentType,  mer spesifikt alle inngående dokumenttyper.
 *
 * @see DokumentType
 */
public class DokumentTypeId {

    // Engangsstønad
    public static final DokumentTypeId SØKNAD_ENGANGSSTØNAD_FØDSEL = new DokumentTypeId("SØKNAD_ENGANGSSTØNAD_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId SØKNAD_ENGANGSSTØNAD_ADOPSJON = new DokumentTypeId("SØKNAD_ENGANGSSTØNAD_ADOPSJON"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_SØKNAD_ENGANGSSTØNAD_FØDSEL = new DokumentTypeId("ETTERSENDT_SØKNAD_ENGANGSSTØNAD_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_SØKNAD_ENGANGSSTØNAD_ADOPSJON = new DokumentTypeId("ETTERSENDT_SØKNAD_ENGANGSSTØNAD_ADOPSJON"); //$NON-NLS-1$

    // Foreldrepenger
    public static final DokumentTypeId SØKNAD_FORELDREPENGER_FØDSEL = new DokumentTypeId("SØKNAD_FORELDREPENGER_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId SØKNAD_FORELDREPENGER_ADOPSJON = new DokumentTypeId("SØKNAD_FORELDREPENGER_ADOPSJON"); //$NON-NLS-1$
    public static final DokumentTypeId FORELDREPENGER_ENDRING_SØKNAD = new DokumentTypeId("FORELDREPENGER_ENDRING_SØKNAD"); //$NON-NLS-1$
    public static final DokumentTypeId FLEKSIBELT_UTTAK_FORELDREPENGER = new DokumentTypeId("FLEKSIBELT_UTTAK_FORELDREPENGER"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_SØKNAD_FORELDREPENGER_ADOPSJON = new DokumentTypeId("ETTERSENDT_SØKNAD_FORELDREPENGER_ADOPSJON"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_SØKNAD_FORELDREPENGER_FØDSEL = new DokumentTypeId("ETTERSENDT_SØKNAD_FORELDREPENGER_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_FLEKSIBELT_UTTAK_FORELDREPENGER = new DokumentTypeId("ETTERSENDT_SØKNAD_FORELDREPENGER_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId ETTERSENDT_FORELDREPENGER_ENDRING_SØKNAD = new DokumentTypeId("ETTERSENDT_FORELDREPENGER_ENDRING_SØKNAD"); //$NON-NLS-1$


    // Svangerskapspenger
    public static final DokumentTypeId SØKNAD_SVANGERSKAPSPENGER = new DokumentTypeId("SØKNAD_SVANGERSKAPSPENGER"); //$NON-NLS-1$

    // Støttedokumenter - Inntekt
    public static final DokumentTypeId INNTEKTSMELDING = new DokumentTypeId("INNTEKTSMELDING"); //$NON-NLS-1$
    public static final DokumentTypeId INNTEKTSOPPLYSNINGER = new DokumentTypeId("INNTEKTSOPPLYSNINGER"); //$NON-NLS-1$

    // Støttedokumenter - Fødsel (Bekreftelse ventet fødselsdato er helt klart mest i bruk i P)
    public static final DokumentTypeId DOKUMENTASJON_AV_TERMIN_ELLER_FØDSEL = new DokumentTypeId("DOKUMENTASJON_AV_TERMIN_ELLER_FØDSEL"); //$NON-NLS-1$
    public static final DokumentTypeId DOKUMENTASJON_AV_OMSORGSOVERTAKELSE = new DokumentTypeId("DOKUMENTASJON_AV_OMSORGSOVERTAKELSE"); //$NON-NLS-1$
    public static final DokumentTypeId BEKREFTELSE_VENTET_FØDSELSDATO = new DokumentTypeId("BEKREFTELSE_VENTET_FØDSELSDATO"); //$NON-NLS-1$
    public static final DokumentTypeId FØDSELSATTEST = new DokumentTypeId("FØDSELSATTEST"); //$NON-NLS-1$

    // Støttedokumenter - Sykdomsrelatert
    public static final DokumentTypeId DOK_INNLEGGELSE = new DokumentTypeId("DOK_INNLEGGELSE"); //$NON-NLS-1$
    public static final DokumentTypeId DOK_MORS_UTDANNING_ARBEID_SYKDOM = new DokumentTypeId("DOK_MORS_UTDANNING_ARBEID_SYKDOM"); //$NON-NLS-1$
    public static final DokumentTypeId LEGEERKLÆRING = new DokumentTypeId("LEGEERKLÆRING"); //$NON-NLS-1$

    // Klage
    public static final DokumentTypeId KLAGE_DOKUMENT = new DokumentTypeId("KLAGE_DOKUMENT"); //$NON-NLS-1$

    // Uspesifikke dokumenter
    public static final DokumentTypeId ANNET = new DokumentTypeId("ANNET"); //$NON-NLS-1$

    public static final DokumentTypeId UDEFINERT = new DokumentTypeId("-"); //$NON-NLS-1$
    private static final Set<String> SØKNAD_TYPER = Set.of(SØKNAD_ENGANGSSTØNAD_FØDSEL.getKode(), SØKNAD_FORELDREPENGER_FØDSEL.getKode(),
            SØKNAD_ENGANGSSTØNAD_ADOPSJON.getKode(), SØKNAD_FORELDREPENGER_ADOPSJON.getKode(), SØKNAD_SVANGERSKAPSPENGER.getKode());
    private static final Set<String> ENDRING_SØKNAD_TYPER = Set.of(FORELDREPENGER_ENDRING_SØKNAD.getKode(), FLEKSIBELT_UTTAK_FORELDREPENGER.getKode());
    private static final Set<String> ANDRE_SPESIAL_TYPER = Set.of(INNTEKTSMELDING.getKode(), KLAGE_DOKUMENT.getKode());

    private static final Set<String> VEDLEGG_TYPER = Set.of(BEKREFTELSE_VENTET_FØDSELSDATO.getKode(), FØDSELSATTEST.getKode(), LEGEERKLÆRING.getKode(),
            DOKUMENTASJON_AV_TERMIN_ELLER_FØDSEL.getKode(), DOKUMENTASJON_AV_OMSORGSOVERTAKELSE.getKode(), DOK_INNLEGGELSE.getKode(), DOK_MORS_UTDANNING_ARBEID_SYKDOM.getKode());


    private String kode;

    public DokumentTypeId(String kode) {
        this.kode = kode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DokumentTypeId that = (DokumentTypeId) o;
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
        return ENDRING_SØKNAD_TYPER.contains(this.kode);
    }

    public boolean erSøknadType() {
        return SØKNAD_TYPER.contains(this.kode);
    }
}

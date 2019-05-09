package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


/**
 * DokumentTypeId er et kodeverk som forvaltes av Kodeverkforvaltning. Det er et subsett av kodeverket DokumentType,  mer spesifikt alle inngående dokumenttyper.
 *
 * @see DokumentType
 */
@Entity(name = "DokumentTypeId")
@DiscriminatorValue(DokumentTypeId.DISCRIMINATOR)
public class DokumentTypeId extends Kodeliste {

    public static final String DISCRIMINATOR = "DOKUMENT_TYPE_ID";
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

    private static final Set<DokumentTypeId> VEDLEGG_TYPER = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            BEKREFTELSE_VENTET_FØDSELSDATO, FØDSELSATTEST, DOKUMENTASJON_AV_TERMIN_ELLER_FØDSEL, DOKUMENTASJON_AV_OMSORGSOVERTAKELSE,
            DOK_INNLEGGELSE, DOK_MORS_UTDANNING_ARBEID_SYKDOM, LEGEERKLÆRING)));
    private static final Set<DokumentTypeId> SØKNAD_TYPER = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SØKNAD_ENGANGSSTØNAD_FØDSEL, SØKNAD_FORELDREPENGER_FØDSEL, SØKNAD_ENGANGSSTØNAD_ADOPSJON, SØKNAD_FORELDREPENGER_ADOPSJON)));
    private static final Set<DokumentTypeId> ENDRING_SØKNAD_TYPER = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            FORELDREPENGER_ENDRING_SØKNAD, FLEKSIBELT_UTTAK_FORELDREPENGER)));
    private static final Set<DokumentTypeId> ANDRE_SPESIAL_TYPER = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            INNTEKTSMELDING, KLAGE_DOKUMENT)));

    public DokumentTypeId() {
        // Hibernate trenger en
    }

    public boolean erSøknadType() {
        return SØKNAD_TYPER.contains(this);
    }

    private DokumentTypeId(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

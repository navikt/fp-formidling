package no.nav.foreldrepenger.melding.behandling.beregningsgrunnlag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "AktivitetStatus")
@DiscriminatorValue(AktivitetStatus.DISCRIMINATOR)
public class AktivitetStatus extends Kodeliste {

    public static final String DISCRIMINATOR = "AKTIVITET_STATUS";

    public static final AktivitetStatus ARBEIDSTAKER = new AktivitetStatus("AT"); //$NON-NLS-1$
    public static final AktivitetStatus FRILANSER = new AktivitetStatus("FL"); //$NON-NLS-1$
    public static final AktivitetStatus TILSTØTENDE_YTELSE = new AktivitetStatus("TY"); //$NON-NLS-1$
    public static final AktivitetStatus SELVSTENDIG_NÆRINGSDRIVENDE = new AktivitetStatus("SN"); //$NON-NLS-1$
    // Kombinert arbeidstaker og frilanser
    public static final AktivitetStatus KOMBINERT_AT_FL = new AktivitetStatus("AT_FL"); //$NON-NLS-1$
    // Kombinert arbeidstaker og selvstendig næringsdrivende
    public static final AktivitetStatus KOMBINERT_AT_SN = new AktivitetStatus("AT_SN"); //$NON-NLS-1$
    // Kombinert frilanser og selvstendig næringsdrivende
    public static final AktivitetStatus KOMBINERT_FL_SN = new AktivitetStatus("FL_SN"); //$NON-NLS-1$
    // Kombinert arbeidstaker, frilanser og selvstendig næringsdrivende
    public static final AktivitetStatus KOMBINERT_AT_FL_SN = new AktivitetStatus("AT_FL_SN"); //$NON-NLS-1$
    public static final AktivitetStatus DAGPENGER = new AktivitetStatus("DP"); //$NON-NLS-1$
    public static final AktivitetStatus ARBEIDSAVKLARINGSPENGER = new AktivitetStatus("AAP"); //$NON-NLS-1$
    public static final AktivitetStatus MILITÆR_ELLER_SIVIL = new AktivitetStatus("MS"); //$NON-NLS-1$
    // Bare ved tilstøtende ytelse
    public static final AktivitetStatus BRUKERS_ANDEL = new AktivitetStatus("BA"); //$NON-NLS-1$
    public static final AktivitetStatus KUN_YTELSE = new AktivitetStatus("KUN_YTELSE");
    public static final AktivitetStatus UDEFINERT = new AktivitetStatus("-"); //$NON-NLS-1$

    private static final Set<AktivitetStatus> AT_STATUSER = new HashSet<>(Arrays.asList(ARBEIDSTAKER,
            KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_AT_FL));

    private static final Set<AktivitetStatus> SN_STATUSER = new HashSet<>(Arrays.asList(SELVSTENDIG_NÆRINGSDRIVENDE,
            KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_FL_SN));

    private static final Set<AktivitetStatus> FL_STATUSER = new HashSet<>(Arrays.asList(FRILANSER,
            KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL, KOMBINERT_FL_SN));

    public AktivitetStatus() {
        // for hibernate
    }

    private AktivitetStatus(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public boolean erArbeidstaker() {
        return AT_STATUSER.contains(this);
    }

    public boolean erSelvstendigNæringsdrivende() {
        return SN_STATUSER.contains(this);
    }

    public boolean erFrilanser() {
        return FL_STATUSER.contains(this);
    }
}

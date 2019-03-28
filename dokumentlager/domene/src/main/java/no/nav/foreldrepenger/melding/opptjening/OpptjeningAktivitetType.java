package no.nav.foreldrepenger.melding.opptjening;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

/**
 * <h3>Internt kodeverk</h3>
 * Definerer aktiviteter benyttet til å vurdere Opptjening.
 * <p>
 * Kodeverket sammenstiller data fra {@link ArbeidType} og {@link RelatertYtelseType}.<br>
 * Senere benyttes dette i mapping til bla. Beregningsgrunnlag.
 */
@Entity(name = "OpptjeningAktivitetType")
@DiscriminatorValue(OpptjeningAktivitetType.DISCRIMINATOR)
public class OpptjeningAktivitetType extends Kodeliste {

    public static final String DISCRIMINATOR = "OPPTJENING_AKTIVITET_TYPE";

    public static final OpptjeningAktivitetType ARBEIDSAVKLARING = new OpptjeningAktivitetType("AAP"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType ARBEID = new OpptjeningAktivitetType("ARBEID"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType DAGPENGER = new OpptjeningAktivitetType("DAGPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType FORELDREPENGER = new OpptjeningAktivitetType("FORELDREPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType FRILANS = new OpptjeningAktivitetType("FRILANS"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType MILITÆR_ELLER_SIVILTJENESTE = new OpptjeningAktivitetType("MILITÆR_ELLER_SIVILTJENESTE"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType NÆRING = new OpptjeningAktivitetType("NÆRING"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType OMSORGSPENGER = new OpptjeningAktivitetType("OMSORGSPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType OPPLÆRINGSPENGER = new OpptjeningAktivitetType("OPPLÆRINGSPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType PLEIEPENGER = new OpptjeningAktivitetType("PLEIEPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType ETTERLØNN_SLUTTPAKKE = new OpptjeningAktivitetType("ETTERLØNN_SLUTTPAKKE"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType SVANGERSKAPSPENGER = new OpptjeningAktivitetType("SVANGERSKAPSPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType SYKEPENGER = new OpptjeningAktivitetType("SYKEPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType UTDANNINGSPERMISJON = new OpptjeningAktivitetType("UTDANNINGSPERMISJON"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType VENTELØNN_VARTPENGER = new OpptjeningAktivitetType("VENTELØNN_VARTPENGER"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType VIDERE_ETTERUTDANNING = new OpptjeningAktivitetType("VIDERE_ETTERUTDANNING"); //$NON-NLS-1$
    public static final OpptjeningAktivitetType UTENLANDSK_ARBEIDSFORHOLD = new OpptjeningAktivitetType("UTENLANDSK_ARBEIDSFORHOLD"); //$NON-NLS-1$

    public static final OpptjeningAktivitetType UDEFINERT = new OpptjeningAktivitetType("-"); //$NON-NLS-1$

    public static final Set<OpptjeningAktivitetType> ANNEN_OPPTJENING = Set.of(VENTELØNN_VARTPENGER, MILITÆR_ELLER_SIVILTJENESTE, ETTERLØNN_SLUTTPAKKE, VIDERE_ETTERUTDANNING, UTENLANDSK_ARBEIDSFORHOLD, FRILANS);

    public OpptjeningAktivitetType() {
    }

    public OpptjeningAktivitetType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}

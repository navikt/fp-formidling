package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "BehandlingType")
@DiscriminatorValue(BehandlingType.DISCRIMINATOR)
public class BehandlingType extends Kodeliste {

    public static final String DISCRIMINATOR = "BEHANDLING_TYPE";

    /**
     * Konstanter for å skrive ned kodeverdi. For å hente ut andre data konfigurert, må disse leses fra databasen (eks.
     * for å hente offisiell kode for et Nav kodeverk).
     */
    public static final BehandlingType FØRSTEGANGSSØKNAD = new BehandlingType("BT-002"); //$NON-NLS-1$
    public static final BehandlingType KLAGE = new BehandlingType("BT-003"); //$NON-NLS-1$
    public static final BehandlingType REVURDERING = new BehandlingType("BT-004"); //$NON-NLS-1$
    public static final BehandlingType INNSYN = new BehandlingType("BT-006"); //$NON-NLS-1$
    public static final BehandlingType TILBAKEKREVING = new BehandlingType("BT-007"); //$NON-NLS-1$
    public static final BehandlingType INNTREKK = new BehandlingType("BT-008"); //$NON-NLS-1$

    /**
     * Alle kodeverk må ha en verdi, det kan ikke være null i databasen. Denne koden gjør samme nytten.
     */
    public static final BehandlingType UDEFINERT = new BehandlingType("-"); //$NON-NLS-1$

    @Transient
    private Integer behandlingstidFristUker;
    @Transient
    private Boolean behandlingstidVarselbrev;

    protected BehandlingType() {
        // Hibernate trenger den
    }

    protected BehandlingType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

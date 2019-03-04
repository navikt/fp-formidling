package no.nav.foreldrepenger.melding.behandling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


/**
 * NB: Pass på! Ikke legg koder vilkårlig her
 * Denne definerer etablerte behandlingstatuser ihht. modell angitt av FFA (Forretning og Fag).
 */
@Entity(name = "BehandlingStatus")
@DiscriminatorValue(BehandlingStatus.DISCRIMINATOR)
public class BehandlingStatus extends Kodeliste {
    public static final String DISCRIMINATOR = "BEHANDLING_STATUS";

    public static final BehandlingStatus AVSLUTTET = new BehandlingStatus("AVSLU"); //$NON-NLS-1$
    public static final BehandlingStatus FATTER_VEDTAK = new BehandlingStatus("FVED"); //$NON-NLS-1$
    public static final BehandlingStatus IVERKSETTER_VEDTAK = new BehandlingStatus("IVED"); //$NON-NLS-1$
    public static final BehandlingStatus OPPRETTET = new BehandlingStatus("OPPRE"); //$NON-NLS-1$
    public static final BehandlingStatus UTREDES = new BehandlingStatus("UTRED"); //$NON-NLS-1$

    private BehandlingStatus(String kode) {
        super(kode, DISCRIMINATOR);
    }

    BehandlingStatus() {
        // Hibernate trenger den
    }

}

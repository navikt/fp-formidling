package no.nav.foreldrepenger.melding.behandling.aksjonspunkt;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name = "ReaktiveringStatus")
@DiscriminatorValue(ReaktiveringStatus.DISCRIMINATOR)
public class ReaktiveringStatus extends Kodeliste {

    public static final String DISCRIMINATOR = "REAKTIVERING_STATUS";

    public static final ReaktiveringStatus AKTIV = new ReaktiveringStatus("AKTIV"); //$NON-NLS-1$
    public static final ReaktiveringStatus INAKTIV = new ReaktiveringStatus("INAKTIV"); //$NON-NLS-1$
    public static final ReaktiveringStatus SLETTET = new ReaktiveringStatus("SLETTET"); //$NON-NLS-1$

    @SuppressWarnings("unused")
    private ReaktiveringStatus() {
        // Hibernate
    }

    public ReaktiveringStatus(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

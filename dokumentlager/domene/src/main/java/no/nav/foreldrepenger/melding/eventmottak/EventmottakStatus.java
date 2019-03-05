package no.nav.foreldrepenger.melding.eventmottak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "EventmottakStatus")
@DiscriminatorValue(EventmottakStatus.DISCRIMINATOR)
public class EventmottakStatus extends Kodeliste {

    public static final String DISCRIMINATOR = "EVENTMOTTAK_STATUS"; //$NON-NLS-1$
    public static final EventmottakStatus FEILET = new EventmottakStatus("FEILET"); //$NON-NLS-1$
    public static final EventmottakStatus FERDIG = new EventmottakStatus("FERDIG"); //$NON-NLS-1$

    EventmottakStatus() {
        // Hibernate trenger den
    }

    protected EventmottakStatus(String kode) {
        super(kode, DISCRIMINATOR);
    }

}


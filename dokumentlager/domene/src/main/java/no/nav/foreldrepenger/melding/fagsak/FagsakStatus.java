package no.nav.foreldrepenger.melding.fagsak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name = "FagsakStatus")
@DiscriminatorValue(FagsakStatus.DISCRIMINATOR)
public class FagsakStatus extends Kodeliste {

    public static final String DISCRIMINATOR = "FAGSAK_STATUS";
    public static final FagsakStatus OPPRETTET = new FagsakStatus("OPPR");
    public static final FagsakStatus UNDER_BEHANDLING = new FagsakStatus("UBEH");
    public static final FagsakStatus AVSLUTTET = new FagsakStatus("AVSLU");
    public static final FagsakStatus DEFAULT = OPPRETTET;

    FagsakStatus() {
        // Hibernate trenger den
    }

    FagsakStatus(String kode) {
        super(kode, DISCRIMINATOR);
    }

}

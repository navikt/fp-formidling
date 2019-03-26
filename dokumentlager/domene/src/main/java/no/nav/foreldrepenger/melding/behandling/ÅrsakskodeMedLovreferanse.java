package no.nav.foreldrepenger.melding.behandling;

import javax.persistence.MappedSuperclass;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@MappedSuperclass
public abstract class ÅrsakskodeMedLovreferanse extends Kodeliste {
    public ÅrsakskodeMedLovreferanse(String kode, String discriminator) {
        super(kode, discriminator);
    }

    protected ÅrsakskodeMedLovreferanse() {
        super();
    }

    public String getLovHjemmelData() {
        return getEkstraData();
    }
}

package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import no.nav.foreldrepenger.melding.kodeverk.diff.IndexKey;

public interface BasisKodeverdi extends IndexKey {
    String getKode();

    String getKodeverk();

    @Override
    default String getIndexKey() {
        return getKode();
    }

}

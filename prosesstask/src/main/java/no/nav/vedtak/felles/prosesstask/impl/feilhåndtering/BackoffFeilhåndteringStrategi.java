package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import no.nav.vedtak.felles.prosesstask.spi.ForsinkelseStrategi;
import no.nav.vedtak.felles.prosesstask.spi.ProsessTaskFeilHåndteringParametere;

public class BackoffFeilhåndteringStrategi implements ForsinkelseStrategi {

    // TODO (FC): definert noen enkle tall. Bør konfigureres eksternt
    final int[] backoffIntervaller = new int[]{30, 30, 30, 60}; // NOSONAR

    @Override
    public int sekunderTilNesteForsøk(int runde, ProsessTaskFeilHåndteringParametere feilhåndteringAlgoritme) {
        return backoffIntervaller[Math.min(runde, backoffIntervaller.length) - 1];
    }
}

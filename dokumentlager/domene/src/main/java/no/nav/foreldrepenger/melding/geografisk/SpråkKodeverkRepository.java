package no.nav.foreldrepenger.melding.geografisk;

import java.util.Optional;

public interface SpråkKodeverkRepository {

    Optional<Språkkode> finnSpråkMedKodeverkEiersKode(String språkkode);
}

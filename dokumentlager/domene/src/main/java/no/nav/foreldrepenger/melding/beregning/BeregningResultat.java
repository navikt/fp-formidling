package no.nav.foreldrepenger.melding.beregning;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BeregningResultat {
    private Set<Beregning> beregninger = new HashSet<>();

    public Optional<Beregning> getSisteBeregning() {
        return beregninger.stream()
                .sorted((b1, b2) -> b2.getBeregnetTidspunkt().compareTo(b1.getBeregnetTidspunkt()))
                .findFirst();
    }
}

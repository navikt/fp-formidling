package no.nav.foreldrepenger.melding.uttak;

import java.util.Set;

public record Saldoer(Set<Stønadskonto> stønadskontoer, int tapteDagerFpff) {
}

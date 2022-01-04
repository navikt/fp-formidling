package no.nav.foreldrepenger.fpformidling.uttak;

import java.util.Set;

public record Saldoer(Set<Stønadskonto> stønadskontoer, int tapteDagerFpff) {
}

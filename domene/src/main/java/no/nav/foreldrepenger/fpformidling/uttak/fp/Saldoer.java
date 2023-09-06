package no.nav.foreldrepenger.fpformidling.uttak.fp;

import java.util.Set;

public record Saldoer(Set<Stønadskonto> stønadskontoer, int tapteDagerFpff) {
}

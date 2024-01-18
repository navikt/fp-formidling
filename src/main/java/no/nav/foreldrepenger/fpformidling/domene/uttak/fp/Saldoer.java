package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

import java.util.Set;

public record Saldoer(Set<Stønadskonto> stønadskontoer, int tapteDagerFpff) {
}

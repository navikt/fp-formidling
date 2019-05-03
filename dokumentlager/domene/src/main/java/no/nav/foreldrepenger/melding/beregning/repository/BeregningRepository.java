package no.nav.foreldrepenger.melding.beregning.repository;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.beregning.Sats;
import no.nav.foreldrepenger.melding.beregning.SatsType;

public interface BeregningRepository {
    Sats finnEksaktSats(SatsType satsType, LocalDate dato);
}

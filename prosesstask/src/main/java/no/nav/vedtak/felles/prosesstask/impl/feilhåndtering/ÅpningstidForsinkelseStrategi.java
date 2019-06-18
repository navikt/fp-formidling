package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import no.nav.vedtak.felles.prosesstask.spi.ForsinkelseStrategi;
import no.nav.vedtak.felles.prosesstask.spi.ProsessTaskFeilHåndteringParametere;
import no.nav.vedtak.util.FPDateUtil;

public class ÅpningstidForsinkelseStrategi implements ForsinkelseStrategi {

    private static final int MINIMUM_FORSINKELSE_SEKUNDER = 120;

    @Override
    public int sekunderTilNesteForsøk(int runde, ProsessTaskFeilHåndteringParametere feilhåndteringAlgoritme) {
        return sekunderTilNesteForsøk(FPDateUtil.nå(), feilhåndteringAlgoritme.getInputVariabel1(), feilhåndteringAlgoritme.getInputVariabel2());
    }

    int sekunderTilNesteForsøk(LocalDateTime now, int klokkeslettÅpning, int klokkeslettStenging) {
        LocalDateTime forsinket = now.plusSeconds(MINIMUM_FORSINKELSE_SEKUNDER);
        if (forsinket.getHour() < klokkeslettÅpning) {
            forsinket = forsinket.withHour(klokkeslettÅpning);
        } else if (forsinket.getHour() >= klokkeslettStenging) {
            forsinket = forsinket.withHour(klokkeslettÅpning).plusDays(1);
        }
        if (forsinket.getDayOfWeek().getValue() > DayOfWeek.FRIDAY.getValue()) {
            forsinket = forsinket.withHour(klokkeslettÅpning).plusDays(1L + DayOfWeek.SUNDAY.getValue() - forsinket.getDayOfWeek().getValue());
        }
        return (int) ChronoUnit.SECONDS.between(now, forsinket);
    }
}

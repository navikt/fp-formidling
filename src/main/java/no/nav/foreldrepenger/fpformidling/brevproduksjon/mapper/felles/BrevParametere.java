package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.Period;

public class BrevParametere {

    private static final int KLAGEFRIST_UKER = 6;
    private static final Period INNHENTING_SVARFRIST = Period.ofWeeks(3);

    private BrevParametere() {
    }

    public static int getKlagefristUker() {
        return KLAGEFRIST_UKER;
    }

    public static Period getSvarfrist() {
        return INNHENTING_SVARFRIST;
    }
}

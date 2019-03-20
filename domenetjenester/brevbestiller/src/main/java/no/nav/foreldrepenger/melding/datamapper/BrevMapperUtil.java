package no.nav.foreldrepenger.melding.datamapper;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.vedtak.util.FPDateUtil;

public class BrevMapperUtil {

    static LocalDate getSvarFrist(BrevParametere brevParametere) {
        return FPDateUtil.iDag().plusDays(brevParametere.getSvarfristDager());
    }
}

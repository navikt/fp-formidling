package no.nav.foreldrepenger.melding.datamapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Strings;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.vedtak.util.FPDateUtil;

public class BrevMapperUtil {
    public static LocalDate getSvarFrist(BrevParametere brevParametere) {
        return FPDateUtil.iDag().plusDays(brevParametere.getSvarfristDager());
    }
}

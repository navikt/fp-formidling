package no.nav.foreldrepenger.melding.datamapper.util;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;

@ApplicationScoped
public class BrevMapperUtil {
    private BrevParametere brevParametere;

    BrevMapperUtil() {
        // CDI
    }

    @Inject
    public BrevMapperUtil(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    public LocalDate getSvarFrist() {
        return LocalDate.now().plusDays(brevParametere.getSvarfristDager());
    }
}

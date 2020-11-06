package no.nav.foreldrepenger.melding.dbstoette;

import static no.nav.foreldrepenger.melding.dbstoette.Databaseskjemainitialisering.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.util.env.Environment;

public class EntityManagerAwareExtension extends no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension {

    private static final Logger LOG = LoggerFactory.getLogger(EntityManagerAwareExtension.class);
    private static final boolean isNotRunningUnderMaven = Environment.current().getProperty("maven.cmd.line.args") == null;

    static {
        if (isNotRunningUnderMaven) {
            LOG.info("Kjører IKKE under maven");
            // prøver alltid migrering hvis endring, ellers funker det dårlig i IDE.
            migrerUnittestSkjemaer();
        }
        settPlaceholdereOgJdniOppslag();
    }
}

package no.nav.foreldrepenger.fpformidling.database;

import java.util.TimeZone;

import no.nav.foreldrepenger.fpformidling.dbstoette.Databaseskjemainitialisering;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class JpaExtension extends EntityManagerAwareExtension {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
        Databaseskjemainitialisering.initUnitTestDataSource();
        Databaseskjemainitialisering.migrerUnittestSkjemaer();
    }
}

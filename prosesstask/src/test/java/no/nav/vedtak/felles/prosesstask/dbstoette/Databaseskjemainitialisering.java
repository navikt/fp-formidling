package no.nav.vedtak.felles.prosesstask.dbstoette;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.nav.vedtak.felles.lokal.dbstoette.DBConnectionProperties;
import no.nav.vedtak.felles.lokal.dbstoette.DatabaseStøtte;

/**
 * Initielt skjemaoppsett + migrering av unittest-skjemaer
 */
public final class Databaseskjemainitialisering {

    private static final Pattern placeholderPattern = Pattern.compile("\\$\\{(.*)\\}");

    public static void main(String[] args) {
        migrerUnittestSkjemaer();
    }

    public static void settOppSkjemaer() {
        try {
            settSchemaPlaceholder(DatasourceConfiguration.UNIT_TEST.getRaw());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void migrerUnittestSkjemaer() {

        settOppSkjemaer();

        try {
            DatabaseStøtte.kjørMigreringFor(DatasourceConfiguration.UNIT_TEST.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void settPlaceholdereOgJdniOppslag() {
        try {
            Databaseskjemainitialisering.settSchemaPlaceholder(DatasourceConfiguration.UNIT_TEST.getRaw());
            DatabaseStøtte.settOppJndiForDefaultDataSource(DatasourceConfiguration.UNIT_TEST.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void kjørMigreringHvisNødvendig() {
        Databaseskjemainitialisering.migrerUnittestSkjemaer();
    }

    public static void settSchemaPlaceholder(List<DBConnectionProperties> connectionProperties) throws FileNotFoundException {
        for (DBConnectionProperties dbcp : connectionProperties) {
            Matcher matcher = placeholderPattern.matcher(dbcp.getSchema());
            if (matcher.matches()) {
                String placeholder = matcher.group(1);
                if (System.getProperty(placeholder) == null) {
                    System.setProperty(placeholder, dbcp.getDefaultSchema());
                }
            }
        }
    }

}

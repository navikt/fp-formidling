package no.nav.vedtak.felles.prosesstask.dbstoette;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import no.nav.vedtak.felles.lokal.dbstoette.DBConnectionProperties;

public enum DatasourceConfiguration {
    UNIT_TEST;

    private String extension;

    DatasourceConfiguration() {
        this(null);
    }

    DatasourceConfiguration(String extension) {
        if (extension != null) {
            this.extension = extension;
        } else {
            this.extension = ".json";
        }
    }

    public List<DBConnectionProperties> get() throws IOException {
        String fileName = this.name().toLowerCase() + extension; // NOSONAR
        try (InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName)) {
            return DBConnectionProperties.fraStream(io);
        }
    }

    public List<DBConnectionProperties> getRaw() throws IOException {
        String fileName = this.name().toLowerCase() + extension; // NOSONAR
        try (InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName)) {
            return DBConnectionProperties.rawFraStream(io);
        }
    }
}

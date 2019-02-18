package no.nav.foreldrepenger.melding.web.server.jetty;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class JettyDevDbKonfigurasjon {
    private static final ObjectMapper OM;

    static {
        OM = new ObjectMapper();
        OM.registerModule(new JavaTimeModule());
        OM.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        OM.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        OM.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }

    private String datasource;
    private String schema;
    private String url;
    private String user;
    private String password;
    private boolean vaultEnable;
    private String vaultRoleprefix;
    private String vaultMountpath;

    JettyDevDbKonfigurasjon() {
    }

    public static List<JettyDevDbKonfigurasjon> fraFil(File file) throws IOException {
        ObjectReader reader = JettyDevDbKonfigurasjon.OM.readerFor(JettyDevDbKonfigurasjon.class);
        try (MappingIterator<JettyDevDbKonfigurasjon> iterator = reader.readValues(file)) {
            return iterator.readAll();
        }
    }

    public String getSchema() {
        return schema;
    }

    public String getDatasource() {
        return datasource;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVaultEnable() {
        return vaultEnable;
    }

    public String getVaultRoleprefix() {
        return vaultRoleprefix;
    }

    public String getVaultMountpath() {
        return vaultMountpath;
    }
}


package no.nav.foreldrepenger.fpformidling.web.server.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropertiesUtils {

    private static String JETTY_SCHEMAS_LOCAL = "jetty_web_server.json";

    private PropertiesUtils() {}

    static List<JettyDevDbKonfigurasjon> getDBConnectionProperties() throws IOException {
        ClassLoader classLoader = PropertiesUtils.class.getClassLoader();
        File file = new File(classLoader.getResource(JETTY_SCHEMAS_LOCAL).getFile());
        return JettyDevDbKonfigurasjon.fraFil(file);
    }

}

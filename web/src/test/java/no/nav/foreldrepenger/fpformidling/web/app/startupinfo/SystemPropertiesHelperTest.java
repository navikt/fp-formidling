package no.nav.foreldrepenger.fpformidling.web.app.startupinfo;

import java.util.HashMap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemPropertiesHelperTest {
    @Test
    public void skal_filtrere_bort_passord_fra_java_opts() {
        var input = new HashMap<String, String>() {{
            put("JAVA_OPTS", "-Djavax.net.ssl.trustStore=/foo/bar -Djavax.net.ssl.trustStorePassword=passord_i_klartekst  -javaagent:/foo/bar/javaagent.jar  -DapplicationName=dummy -");
        }};

        SystemPropertiesHelper.filter(input);

        Assertions.assertThat(input.get("JAVA_OPTS")).isEqualTo("-Djavax.net.ssl.trustStore=/foo/bar -Djavax.net.ssl.trustStorePassword=*****  -javaagent:/foo/bar/javaagent.jar  -DapplicationName=dummy -");
    }
}

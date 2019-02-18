package no.nav.foreldrepenger.melding.web.app.konfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;
import no.nav.foreldrepenger.melding.web.app.tjenester.NaisRestTjeneste;
import no.nav.foreldrepenger.melding.web.app.tjenester.SelftestRestTjeneste;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    public static final String API_URL = "internal";

    public InternalApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        if (utviklingServer()) {
            beanConfig.setSchemes(new String[]{"http"});
        } else {
            beanConfig.setSchemes(new String[]{"https"});
        }

        beanConfig.setBasePath("/fpformidling/" + API_URL);
        beanConfig.setResourcePackage("no.nav");
        beanConfig.setTitle("Vedtaksløsningen - Formidling");
        beanConfig.setDescription("REST grensesnitt for Vedtaksløsningen.");
        beanConfig.setScan(true);
    }

    /**
     * Finner ut av om vi kjører utviklingsserver. Settes i JettyDevServer#konfigurerMiljø()
     *
     * @return true dersom utviklingsserver.
     */
    private boolean utviklingServer() {
        return Boolean.getBoolean("develop-local");
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(NaisRestTjeneste.class);
        classes.add(SelftestRestTjeneste.class);

        return Collections.unmodifiableSet(classes);
    }

}

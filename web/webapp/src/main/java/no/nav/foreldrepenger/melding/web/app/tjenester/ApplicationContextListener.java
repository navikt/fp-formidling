package no.nav.foreldrepenger.melding.web.app.tjenester;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Starter interne applikasjontjenester
 */
public class ApplicationContextListener implements ServletContextListener {

    @Inject
    private ApplicationServiceStarter applicationServiceStarter; //NOSONAR - vil ikke fungere med constructor innjection

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        applicationServiceStarter.startServices();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        applicationServiceStarter.stopServices();
    }
}

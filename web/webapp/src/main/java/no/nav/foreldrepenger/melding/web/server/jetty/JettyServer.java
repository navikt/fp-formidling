package no.nav.foreldrepenger.melding.web.server.jetty;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;

import no.nav.foreldrepenger.melding.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.melding.web.server.jetty.db.DataSourceKonfig;
import no.nav.foreldrepenger.melding.web.server.jetty.db.DatabaseScript;
import no.nav.vedtak.isso.IssoApplication;

public class JettyServer extends AbstractJettyServer {

    DataSourceKonfig dataSourceKonfig;

    public JettyServer() {
        this(new JettyWebKonfigurasjon());
    }

    public JettyServer(int serverPort) {
        this(new JettyWebKonfigurasjon(serverPort));
    }

    JettyServer(AppKonfigurasjon appKonfigurasjon) {
        super(appKonfigurasjon);
    }

    public static void main(String[] args) throws Exception {
        JettyServer jettyServer;
        if (args.length > 0) {
            int serverPort = Integer.parseUnsignedInt(args[0]);
            jettyServer = new JettyServer(serverPort);
        } else {
            jettyServer = new JettyServer();
        }
        jettyServer.bootStrap();
    }

    @Override
    protected void konfigurerMiljø() throws Exception {
        konfigurerDataSourceKonfig();
        hacks4Nais();
    }

    protected void konfigurerDataSourceKonfig() {
        dataSourceKonfig = new DataSourceKonfig();
    }

    private void hacks4Nais() {
        loadBalancerFqdnTilLoadBalancerUrl();
        temporært();
    }

    private void loadBalancerFqdnTilLoadBalancerUrl() {
        if (System.getenv("LOADBALANCER_FQDN") != null) {
            String loadbalancerFqdn = System.getenv("LOADBALANCER_FQDN");
            String protocol = (loadbalancerFqdn.startsWith("localhost")) ? "http" : "https";
            System.setProperty("loadbalancer.url", protocol + "://" + loadbalancerFqdn);
        }
    }

    private void temporært() {
        // FIXME (u139158): PFP-1176 Skriv om i OpenAmIssoHealthCheck og AuthorizationRequestBuilder når Jboss dør
        if (System.getenv("OIDC_OPENAM_HOSTURL") != null) {
            System.setProperty("OpenIdConnect.issoHost", System.getenv("OIDC_OPENAM_HOSTURL"));
        }
        // FIXME (u139158): PFP-1176 Skriv om i AuthorizationRequestBuilder og IdTokenAndRefreshTokenProvider når Jboss dør
        if (System.getenv("OIDC_OPENAM_AGENTNAME") != null) {
            System.setProperty("OpenIdConnect.username", System.getenv("OIDC_OPENAM_AGENTNAME"));
        }
        // FIXME (u139158): PFP-1176 Skriv om i IdTokenAndRefreshTokenProvider når Jboss dør
        if (System.getenv("OIDC_OPENAM_PASSWORD") != null) {
            System.setProperty("OpenIdConnect.password", System.getenv("OIDC_OPENAM_PASSWORD"));
        }
    }

    @Override
    protected void konfigurerJndi() throws Exception {
        new EnvEntry("jdbc/defaultDS", dataSourceKonfig.getDefaultDatasource());
    }

    @Override
    protected void migrerDatabaser() throws IOException {
        new DatabaseScript(dataSourceKonfig.getMigrationDatasource(), dataSourceKonfig.getMigrationScripts()).migrate();
    }

    @Override
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        WebAppContext webAppContext = super.createContext(appKonfigurasjon);
        webAppContext.setParentLoaderPriority(true);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        List<Class<?>> appClasses = Arrays.asList((Class<?>) ApplicationConfig.class, (Class<?>) IssoApplication.class);

        List<Resource> resources = appClasses.stream().map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation())).collect(Collectors.toList());

        metaData.setWebInfClassesDirs(resources);
    }

    @Override
    protected ResourceCollection createResourceCollection() throws IOException {
        return new ResourceCollection(
                Resource.newClassPathResource("/web")
        );
    }

}

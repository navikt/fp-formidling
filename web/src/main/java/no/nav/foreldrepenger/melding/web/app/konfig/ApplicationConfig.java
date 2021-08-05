package no.nav.foreldrepenger.melding.web.app.konfig;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.melding.web.app.exceptions.KnownExceptionMappers;
import no.nav.foreldrepenger.melding.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.melding.web.app.tjenester.ForvaltningRestTjeneste;
import no.nav.foreldrepenger.melding.web.app.tjenester.brev.BrevRestTjeneste;
import no.nav.foreldrepenger.melding.web.server.jetty.TimingFilter;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends ResourceConfig {

    static final String API_URI = "/api";

    public ApplicationConfig() {

        try {
            property(org.glassfish.jersey.server.ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(new SwaggerConfiguration()
                            .openAPI(new OpenAPI()
                                    .info(new Info()
                                            .title("Foreldrepenger formidling")
                                            .version("1.0")
                                            .description("REST grensesnitt for formidling."))
                                    .addServersItem(new Server()
                                            .url("/fpformidling")))
                            .prettyPrint(true)
                            .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                            .resourcePackages(Stream.of("no.nav")
                                    .collect(Collectors.toSet())))
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        register(SwaggerSerializers.class);
        register(OpenApiResource.class);
        register(JacksonJsonConfig.class);
        register(TimingFilter.class);

        registerClasses(Set.of(BrevRestTjeneste.class,
                ForvaltningRestTjeneste.class,
                ProsessTaskRestTjeneste.class));

        registerInstances(new LinkedHashSet<>(new KnownExceptionMappers().getExceptionMappers()));

        property(org.glassfish.jersey.server.ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
    }
}

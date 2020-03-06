package no.nav.foreldrepenger.melding.web.app.konfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.melding.web.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.melding.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.melding.web.app.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.melding.web.app.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.melding.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.melding.web.app.tjenester.ForvaltningRestTjeneste;
import no.nav.foreldrepenger.melding.web.app.tjenester.brev.BrevRestTjeneste;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    public ApplicationConfig() {

        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title("Foreldrepenger risikoklassifisering")
                .version("1.0")
                .description("REST grensesnitt for risikoklassifisering.");

        oas.info(info)
                .addServersItem(new Server()
                        .url("/fpformidling"));
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                .resourcePackages(Stream.of("no.nav")
                        .collect(Collectors.toSet()));

        try {
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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

        classes.add(BrevRestTjeneste.class);
        classes.add(ForvaltningRestTjeneste.class);
        classes.add(ProsessTaskRestTjeneste.class);

        classes.add(GeneralRestExceptionMapper.class);
        classes.add(JacksonJsonConfig.class);

        classes.add(SwaggerSerializers.class);
        classes.add(OpenApiResource.class);
        classes.add(ConstraintViolationMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(JsonParseExceptionMapper.class);

        return Collections.unmodifiableSet(classes);
    }
}

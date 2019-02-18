package no.nav.foreldrepenger.melding.web.local.development;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/jetty")
public class LocalDevelopmentApplication extends Application {
    //FIXME (u139158): Denne pakken skal ligge i src/test, men sliter litt med å få det til :(
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        classes.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return classes;
    }
}
package no.nav.foreldrepenger.melding.web.app.konfig;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import io.swagger.jaxrs.listing.ApiListingResource;
import no.nav.vedtak.felles.integrasjon.felles.ws.SoapWebService;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

public class SoapApiTester {

    static final List<Class<?>> UNNTATT = Collections.singletonList(ApiListingResource.class);

    static Collection<Method> finnAlleSoapMetoder() {
        List<Method> liste = new ArrayList<>();
        for (Class<?> klasse : finnAlleSoapTjenester()) {
            for (Method method : klasse.getDeclaredMethods()) {
                if (method.getAnnotation(BeskyttetRessurs.class) == null && Modifier.isPublic(method.getModifiers()) && !method.getName().equals("ping")) {
                    liste.add(method);
                }
            }
        }
        return liste;
    }

    private static List<Class<?>> getAllClasses() {
        Reflections reflections = new Reflections("no.nav.foreldrepenger", new SubTypesScanner(false));
        Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);
        return new ArrayList<>(classes);
    }

    static List<Class<?>> finnAlleSoapTjenester() {
        List<Class<?>> classes = getAllClasses();
        List<Class<?>> classesToReturn = new ArrayList<Class<?>>();

        classes.stream().filter(s -> s.getAnnotation(SoapWebService.class) != null).forEach(s -> classesToReturn.add(s));
        return classesToReturn;
    }
}

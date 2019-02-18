package no.nav.foreldrepenger.melding.web.app.konfig;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;


public class SoapApiAbacTest {

    private static Collection<Method> soapMethods;

    @BeforeClass
    public static void init() {
        soapMethods = SoapApiTester.finnAlleSoapMetoder();
    }

    /**
     * IKKE ignorer denne testen, sikrer at SOAP-endepunkter får tilgangskontroll
     * <p>
     * Kontakt Team Humle hvis du trenger hjelp til å endre koden din slik at den går igjennom her     *
     */
    @Test
    public void test_at_alle_soapmetoder_er_annotert_med_BeskyttetRessurs() throws Exception {
        for (Method soapMethod : SoapApiTester.finnAlleSoapMetoder()) {
            if (soapMethod.getAnnotation(BeskyttetRessurs.class) == null) {
                throw new AssertionError("Mangler @" + BeskyttetRessurs.class.getSimpleName() + "-annotering på " + soapMethod);
            }
        }
    }

    /**
     * IKKE ignorer denne testen, helper til med at input til tilgangskontroll blir riktig
     * <p>
     * Kontakt Team Humle hvis du trenger hjelp til å endre koden din slik at den går igjennom her
     */
    @Test
    public void test_at_alle_input_parametre_til_soapmetoder_er_annotert_med_TilpassetAbacAttributt() {
        String feilmelding = "Parameter type %s på metode %s.%s må ha annotering " + TilpassetAbacAttributt.class.getSimpleName() + ".\n";
        StringBuilder feilmeldinger = new StringBuilder();
        for (Method soapMethod : soapMethods) {
            for (Parameter parameter : soapMethod.getParameters()) {
                if (parameter.getAnnotation(TilpassetAbacAttributt.class) == null) {
                    feilmeldinger.append(String.format(feilmelding, parameter.getType().getSimpleName(), soapMethod.getDeclaringClass().getSimpleName(), soapMethod.getName()));
                }
            }
        }

        if (feilmeldinger.length() > 0) {
            throw new AssertionError("Følgende inputparametre til SOAP-tjenester passerte ikke validering\n" + feilmeldinger);
        }

    }
}

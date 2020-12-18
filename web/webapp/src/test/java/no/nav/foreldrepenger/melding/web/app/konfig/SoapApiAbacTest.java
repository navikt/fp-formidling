package no.nav.foreldrepenger.melding.web.app.konfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;

public class SoapApiAbacTest {

    private static final Collection<Method> soapMethods = SoapApiTester.finnAlleSoapMetoder();

    /**
     * IKKE ignorer denne testen, sikrer at SOAP-endepunkter får tilgangskontroll
     * <p>
     * Spør på Slack hvis du trenger hjelp til å endre koden din slik at den går igjennom her
     */
    @Test
    public void test_at_alle_soapmetoder_er_annotert_med_BeskyttetRessurs() {
        String feilmelding = "Mangler @%s-annotering på %s";
        StringBuilder feilmeldinger = new StringBuilder();
        for (Method soapMethod : SoapApiTester.finnAlleSoapMetoder()) {
            if (soapMethod.getAnnotation(BeskyttetRessurs.class) == null) {
                feilmeldinger.append(String.format(feilmelding, BeskyttetRessurs.class.getSimpleName(), soapMethod));
            }
        }

        assertThat(feilmeldinger).as("Følgende SOAP-tjenester passerte ikke validering:\n" + feilmeldinger).hasSize(0);
    }

    /**
     * IKKE ignorer denne testen, helper til med at input til tilgangskontroll blir riktig
     * <p>
     * Spør på Slack hvis du trenger hjelp til å endre koden din slik at den går igjennom her
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

        assertThat(feilmeldinger).as("Følgende inputparametre til SOAP-tjenester passerte ikke validering:\n" + feilmeldinger).hasSize(0);
    }
}

package no.nav.foreldrepenger.melding.brev.fritekstmal;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public interface BrevmalKildefiler {
    String TEMPLATES_PATH = "src/main/java/no/nav/foreldrepenger/melding/brev/fritekstmal/templates/";
    String RESOURCE_BUNDLE_ROOT = "dokumentmal/";
    String OVERSKRIFT = "overskrift";
    String BRØDTEKST = "brødtekst";

    static String getLocaleSuffixFor(Språkkode språkkode) {
        return Språkkode.nn.getKode().equals(språkkode.getKode()) ?
                "nn_NO" : Språkkode.en.getKode().equals(språkkode.getKode()) ?
                "en_GB" :
                "nb_NO";
    }
}

package no.nav.foreldrepenger.melding.datamapper.mal.fritekst;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public interface BrevmalKildefiler {
    String TEMPLATES_ROOT = "dokumentmal/";
    String SHARED = "felles";
    String OVERSKRIFT = "overskrift";
    String BRØDTEKST = "brødtekst";

    static String getLocaleSuffixFor(Språkkode språkkode) {
        return Språkkode.nn.getKode().equals(språkkode.getKode()) ?
                "nn_NO" : Språkkode.en.getKode().equals(språkkode.getKode()) ?
                "en_GB" :
                "nb_NO";
    }

    static String getPathTo(String brevmalMappe) {
        return BrevmalKildefiler.class.getClassLoader().getResource(TEMPLATES_ROOT + brevmalMappe).toExternalForm()
            .replaceFirst("file:/", "");
    }
}

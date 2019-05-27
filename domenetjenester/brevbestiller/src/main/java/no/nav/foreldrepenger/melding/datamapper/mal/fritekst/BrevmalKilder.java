package no.nav.foreldrepenger.melding.datamapper.mal.fritekst;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public interface BrevmalKilder {
    String ROTMAPPE = "dokumentmal";
    String FELLES = "felles";
    String OVERSKRIFT = "overskrift";
    String BRØDTEKST = "brødtekst";

    default String getLocaleSuffixFor(Språkkode språkkode) {
        return Språkkode.nn.getKode().equals(språkkode.getKode()) ?
                "nn_NO" : Språkkode.en.getKode().equals(språkkode.getKode()) ?
                "en_GB" :
                "nb_NO";
    }

    default String getClassPath(String... directories) {
        return String.join("/", directories);
    }

    default String getResourceBundle(String brevmal) {
        return getClassPath(ROTMAPPE, brevmal, brevmal);
    }
}

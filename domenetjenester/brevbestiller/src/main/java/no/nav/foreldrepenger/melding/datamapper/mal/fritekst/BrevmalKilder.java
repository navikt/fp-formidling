package no.nav.foreldrepenger.melding.datamapper.mal.fritekst;

import java.util.Locale;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public interface BrevmalKilder {
    String ROTMAPPE = "dokumentmal/";
    String FELLES = "felles";
    String OVERSKRIFT = "overskrift";
    String BRØDTEKST = "brødtekst";

    String templateFolder();

    default String getTemplateClassPath() {
        return "/" + ROTMAPPE + templateFolder();
    }

    default String getFellesClassPath() {
        return "/" +  ROTMAPPE + FELLES;
    }

    static String getResourceBundle(String brevmal) {
        return ROTMAPPE + brevmal + "/" + brevmal;
    }

    static String getLocaleSuffixFor(Språkkode språkkode) {
        return Språkkode.nn.getKode().equals(språkkode.getKode()) ?
                "nn_NO" : Språkkode.en.getKode().equals(språkkode.getKode()) ?
                "en_GB" :
                "nb_NO";
    }

    static Locale getLocale(Språkkode språkkode) {
        return Språkkode.nn.getKode().equals(språkkode.getKode()) ?
                new Locale("nn", "NO") : Språkkode.en.getKode().equals(språkkode.getKode()) ?
                new Locale("en", "GB") :
                new Locale("nb", "NO");
    }
}

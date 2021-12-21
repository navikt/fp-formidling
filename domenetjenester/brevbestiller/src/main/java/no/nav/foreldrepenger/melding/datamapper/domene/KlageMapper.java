package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemler;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGET_FOR_SENT;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;

public class KlageMapper {

    public static List<KlageAvvistÅrsak> listeAvAvvisteÅrsaker(Klage klage) {
        if (klage.getFormkravKA() != null) {
            return klage.getFormkravKA().avvistÅrsaker();
        } else if (klage.getFormkravNFP() != null) {
            return klage.getFormkravNFP().avvistÅrsaker();
        }
        return Collections.emptyList();
    }

    public static Optional<String> hentOgFormaterLovhjemlerForAvvistKlage(Klage klage, Språkkode språkkode) {
        Set<String> klagehjemler = hentKlageHjemler(klage);
        boolean klagetEtterKlagefrist = listeAvAvvisteÅrsaker(klage).stream()
                .anyMatch(KLAGET_FOR_SENT::equals);
        return formaterLovhjemlerForAvvistKlage(klagehjemler, klagetEtterKlagefrist, språkkode);
    }

    static Set<String> hentKlageHjemler(Klage klage) {
        Set<String> klageHjemler = new TreeSet<>();
        String klageVurdertAv = klage.getFormkravKA() != null ? "KA" : "NFP";
        listeAvAvvisteÅrsaker(klage).forEach(årsak -> klageHjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsak, klageVurdertAv)));
        return klageHjemler;
    }

    static Optional<String> formaterLovhjemlerForAvvistKlage(Set<String> hjemler, boolean klagetEtterKlagefrist, Språkkode språkkode) {
        String startTillegg;
        if (Språkkode.NN.equals(språkkode)) {
            startTillegg = klagetEtterKlagefrist ?
                    "folketrygdlova § 21-12 og forvaltningslova" : "forvaltningslova";
        } else {
            startTillegg = klagetEtterKlagefrist ?
                    "folketrygdloven § 21-12 og forvaltningsloven" : "forvaltningsloven";
        }
        StringBuilder lovhjemmelBuiloer = new StringBuilder();
        int antallLovreferanser = formaterLovhjemler(hjemler, lovhjemmelBuiloer, startTillegg, null);
        if (antallLovreferanser == 0) {
            return Optional.empty();
        }
        return Optional.of(lovhjemmelBuiloer.toString());
    }

    public static boolean erOpphevet(Klage klage, DokumentHendelse hendelse) {
        if (hendelse.getErOpphevetKlage()) {
            return true;
        }
        if (klage.getGjeldendeKlageVurderingsresultat() == null) {
            throw new IllegalStateException();
        }
        KlageVurdering klageVurdering = klage.getGjeldendeKlageVurderingsresultat().klageVurdering();
        return KlageVurdering.OPPHEVE_YTELSESVEDTAK.equals(klageVurdering);
    }
}

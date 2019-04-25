package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemler;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_KONKRET;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_SIGNERT;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGER_IKKE_PART;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGET_FOR_SENT;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGE_UGYLDIG;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;

public class KlageMapper {
    public static Map<String, AvvistGrunnKode> avvistGrunnMap;

    static {
        avvistGrunnMap = new HashMap<>();
        avvistGrunnMap.put(KLAGET_FOR_SENT.getKode(), AvvistGrunnKode.ETTER_6_UKER);
        avvistGrunnMap.put(KLAGE_UGYLDIG.getKode(), AvvistGrunnKode.KLAGEUGYLDIG);
        avvistGrunnMap.put(IKKE_PAKLAGD_VEDTAK.getKode(), AvvistGrunnKode.KLAGEIKKEVEDTAK);
        avvistGrunnMap.put(KLAGER_IKKE_PART.getKode(), AvvistGrunnKode.KLAGEIKKEPART);
        avvistGrunnMap.put(IKKE_KONKRET.getKode(), AvvistGrunnKode.KLAGEIKKEKONKRET);
        avvistGrunnMap.put(IKKE_SIGNERT.getKode(), AvvistGrunnKode.KLAGEUGYLDIG);
    }

    public static List<KlageAvvistÅrsak> listeAvAvvisteÅrsaker(Klage klage) {
        if (klage.getFormkravKA() != null) {
            return klage.getFormkravKA().getAvvistÅrsaker();
        } else if (klage.getFormkravNFP() != null) {
            return klage.getFormkravNFP().getAvvistÅrsaker();
        }
        return Collections.emptyList();
    }

    public static Optional<String> hentOgFormaterLovhjemlerForAvvistKlage(Klage klage) {
        Set<String> klagehjemler = hentKlageHjemler(klage);
        boolean klagetEtterKlagefrist = listeAvAvvisteÅrsaker(klage).stream()
                .anyMatch(KLAGET_FOR_SENT::equals);
        return formaterLovhjemlerForAvvistKlage(klagehjemler, klagetEtterKlagefrist);
    }

    static Set<String> hentKlageHjemler(Klage klage) {
        Set<String> klageHjemler = new TreeSet<>();
        String klageVurdertAv = klage.getFormkravKA() != null ? "KA" : "NFP";
        listeAvAvvisteÅrsaker(klage).forEach(årsak -> klageHjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsak, klageVurdertAv)));
        return klageHjemler;
    }

    static Optional<String> formaterLovhjemlerForAvvistKlage(Set<String> hjemler, boolean klagetEtterKlagefrist) {
        String startTillegg = klagetEtterKlagefrist ?
                "folketrygdloven § 21-12 og forvaltningsloven" : "forvaltningsloven";
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
        KlageVurdering klageVurdering = null;
        if (klage.getKlageVurderingResultatNFP() != null) {
            klageVurdering = klage.getKlageVurderingResultatNFP().getKlageVurdering();
        } else if (klage.getKlageVurderingResultatNK() != null) {
            klageVurdering = klage.getKlageVurderingResultatNK().getKlageVurdering();
        }
        if (klageVurdering == null) {
            throw new IllegalStateException();
        }
        return KlageVurdering.OPPHEVE_YTELSESVEDTAK.equals(klageVurdering);
    }
}

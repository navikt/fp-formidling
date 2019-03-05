package no.nav.foreldrepenger.melding.datamapper.mal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.util.StringUtils;

public final class DokumentMalFelles {

    private DokumentMalFelles() {
        // Skal ikke instansieres
    }

    protected static Flettefelt opprettFlettefelt(String feltnavn, String feltverdi) {
        Flettefelt f = new Flettefelt();
        f.setFeltnavn(feltnavn);
        f.setFeltverdi(feltverdi);
        return f;
    }

    protected static void opprettIkkeObligatoriskeFlettefelt(List<Flettefelt> flettefelter, String feltnavn, String feltverdi) {
        if (!StringUtils.nullOrEmpty(feltverdi)) {
            Flettefelt flettefelt = new Flettefelt();

            flettefelt.setFeltnavn(feltnavn);
            flettefelt.setFeltverdi(feltverdi);

            flettefelter.add(flettefelt);
        }
    }

    static Flettefelt opprettStrukturertFlettefelt(String feltnavn, Object feltverdi) {
        Flettefelt f = new Flettefelt();
        f.setFeltnavn(feltnavn);
        f.setStukturertVerdi(feltverdi);
        return f;
    }

    static List<Flettefelt> opprettStrukturertFlettefeltListe(String feltnavn, List<?> feltverdier) {
        List<Flettefelt> liste = new ArrayList<>();
        int nummer = 0;
        for (Object feltverdi : feltverdier) {
            Flettefelt f = new Flettefelt();
            f.setFeltnavn(feltnavn + ":" + nummer);
            f.setStukturertVerdi(feltverdi);
            liste.add(f);
            nummer++;
        }
        return liste;
    }

    static List<Flettefelt> opprettStrukturertFlettefeltListe(String feltnavn, Set<?> feltverdier) {
        return opprettStrukturertFlettefeltListe(feltnavn, new ArrayList<>(feltverdier));
    }

    static Flettefelt opprettObligatoriskeFlettefelt(String feltnavn, Object feltverdi) {
        try {
            return opprettFlettefelt(feltnavn, feltverdi.toString());
        } catch (RuntimeException e) { //NOSONAR
            throw FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi(feltnavn).toException();
        }
    }

    static Flettefelt opprettObligatoriskeStrukturertFlettefelt(String feltnavn, Object feltverdi) {
        try {
            return opprettStrukturertFlettefelt(feltnavn, feltverdi.toString());
        } catch (RuntimeException e) { //NOSONAR
            throw FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi(feltnavn).toException();
        }
    }
}

package no.nav.foreldrepenger.melding.datamapper.mal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.nav.vedtak.exception.TekniskException;

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
        if (feltverdi != null && !feltverdi.isEmpty()) {
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
            throw new TekniskException("FPFORMIDLING-220913",
            String.format("Kan ikke produsere dokument, obligatorisk felt %s mangler innhold", feltnavn), e);
        }
    }

    static Flettefelt opprettObligatoriskeStrukturertFlettefelt(String feltnavn, Object feltverdi) {
        try {
            return opprettStrukturertFlettefelt(feltnavn, feltverdi.toString());
        } catch (RuntimeException e) { //NOSONAR
            throw new TekniskException("FPFORMIDLING-220913",
            String.format("Kan ikke produsere dokument, obligatorisk felt %s mangler innhold", feltnavn), e);
        }
    }
}

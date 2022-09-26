package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class LovhjemmelComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 7736396250271405587L;

    @Override
    public int compare(String hjemmel1, String hjemmel2) {
        var lovOgParagraf1 = hjemmel1.split("-|\\s");
        var lovOgParagraf2 = hjemmel2.split("-|\\s");
        if (!gyldigLovOgParagraf(lovOgParagraf1)) {
            return -1;
        } else if (!gyldigLovOgParagraf(lovOgParagraf2)) {
            return 1;
        }

        var lovResultat = sammenlikneKapittel(lovOgParagraf1, lovOgParagraf2);
        if (lovResultat == 0) {
            // Likt kapittel - sjekker antall deler:
            lovResultat = Integer.compare(lovOgParagraf1.length, lovOgParagraf2.length);
            if ((lovResultat == 0 && lovOgParagraf1.length > 1)) { // Likt antall deler
                lovResultat = sammenlikneParagraf(lovOgParagraf1, lovOgParagraf2);
                if (lovResultat == 0 && lovOgParagraf1.length > 2) {
                    // Lik paragraf, og begge har bokstav:
                    lovResultat = sammenlikneBokstav(lovOgParagraf1, lovOgParagraf2);
                }
            } else if (lovResultat != 0 && lovOgParagraf1.length > 1 && lovOgParagraf2.length > 1) { // Ulikt antall deler
                lovResultat = sammenlikneParagraf(lovOgParagraf1, lovOgParagraf2);
                if (lovResultat == 0) {
                    // Lik paragraf, men bare en av dem har bokstav:
                    lovResultat = Integer.compare(lovOgParagraf1.length, lovOgParagraf2.length);
                }
            }
        }
        return lovResultat;
    }

    private boolean gyldigLovOgParagraf(String... hjemmel) {
        return Arrays.stream(hjemmel).allMatch(s -> s.matches("-?\\d+|(\\w)?"));
    }

    private int sammenlikneKapittel(String[] lovOgParagraf1, String[] lovOgParagraf2) {
        // Sammenlikner tall nr 1 / kapittel:
        return Integer.compare(Integer.parseInt(lovOgParagraf1[0]), Integer.parseInt(lovOgParagraf2[0]));
    }

    private int sammenlikneParagraf(String[] lovOgParagraf1, String[] lovOgParagraf2) {
        // Sammenlikner tall nr 2 / paragraf, feks 9-1 mot 9-2:
        return Integer.compare(Integer.parseInt(lovOgParagraf1[1]), Integer.parseInt(lovOgParagraf2[1]));
    }

    private int sammenlikneBokstav(String[] lovOgParagraf1, String[] lovOgParagraf2) {
        // Sammenlikner bokstaven bak paragrafen, feks 9-2 a mot 9-2 b:
        return lovOgParagraf1[2].compareTo(lovOgParagraf2[2]);
    }
}

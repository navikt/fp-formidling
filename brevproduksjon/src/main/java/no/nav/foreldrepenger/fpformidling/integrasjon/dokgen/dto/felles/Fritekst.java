package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.klage.Klage;

/**
 * Hjelpeklasse for håndtering av fritekst som sikrer at formatering blir riktig i Dokgen.
 */
public class Fritekst {

    @JsonValue
    private String fritekst;

    private Fritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public String getFritekst() {
        return fritekst;
    }

    public static Optional<Fritekst> fra(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (dokumentHendelse.getFritekst() != null && !dokumentHendelse.getFritekst().isEmpty()) {
            return Optional.of(fra(dokumentHendelse.getFritekst()));
        } else if (behandling.getBehandlingsresultat() != null && behandling.getBehandlingsresultat().getAvslagarsakFritekst() != null) {
            return Optional.of(fra(behandling.getBehandlingsresultat().getAvslagarsakFritekst()));
        }
        return Optional.empty();
    }

    public static Optional<Fritekst> fra(DokumentHendelse dokumentHendelse, Klage klage) {
        if (dokumentHendelse.getFritekst() != null && !dokumentHendelse.getFritekst().isEmpty()) {
            return Optional.of(fra(dokumentHendelse.getFritekst()));
        } else if (klage.getGjeldendeKlageVurderingsresultat() != null && klage.getGjeldendeKlageVurderingsresultat().fritekstTilBrev() != null) {
            return Optional.of(fra(klage.getGjeldendeKlageVurderingsresultat().fritekstTilBrev()));
        }
        return Optional.empty();
    }

    public static Fritekst fra(String fritekst) {
        if (fritekst == null) {
            return null;
        }
        fritekst = konverterOverskrifterTilDokgenFormat(fritekst);
        fritekst = ivaretaLinjeskiftIFritekst(fritekst);
        fritekst = fiksNavNoLinker(fritekst);
        return new Fritekst(fritekst);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Fritekst) object;
        return Objects.equals(fritekst, that.fritekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fritekst);
    }

    @Override
    public String toString() {
        return "Fritekst{" +
                "fritekst=" + fritekst +
                '}';
    }

    // I Dokprod brukes _ til overskrift. Metoden viderefører dette ved å konvertere til ##### for Dokgen:
    private static String konverterOverskrifterTilDokgenFormat(String fritekst) {
        String[] linjer = fritekst.split("\n");
        StringBuilder resultat = new StringBuilder();
        boolean førsteLinje = true;

        for (String linje : linjer) {
            if (!førsteLinje) {
                resultat.append("\n");
            } else {
                førsteLinje = false;
            }
            resultat.append(linje.replaceAll("^_", "##### "));
        }
        return resultat.toString();
    }

    // Enkelt-linjeskift blir ignorert. Metoden sørger for at de kommer med ved å legge til ekstra der det trengs:
    static String ivaretaLinjeskiftIFritekst(String fritekst) {
        if (fritekst.equals("")) {
            return fritekst;
        }
        String[] linjer = fritekst.split("\n");
        if (linjer.length <= 1) {
            return fritekst;
        }

        StringBuilder resultat = new StringBuilder();
        resultat.append(linjer[0]);
        for (int i = 1; i < linjer.length; i++) {
            String linje = linjer[i];
            String forrigeLinje = linjer[i-1];

            if (forrigeLinje.startsWith("#####") || linje.startsWith("#####") || linje.startsWith("- ") || forrigeLinje.equals("")) {
                // Overskrifter og punktlister skal bare ha et enkelt linjeskift for at det skal bli riktig
                resultat.append("\n");
            } else if (linje.equals("") || (forrigeLinje.startsWith("- ") && !linje.startsWith("- "))) {
                // Tom linje (= ønske om mer "luft") og første linje etter punktliste må få et ekstra linjeskift
                resultat.append("\n\n");
            } else {
                // Standard linjeskift med lite "luft", feks midt i setninger som er copy-pastet fra PDF
                resultat.append("\\\n");
            }

            resultat.append(linje);
        }
        return resultat.toString();
    }

    // Når linker copy-pastes inn i fritekst er de ikke lengre klikkbare og må derfor fikses:
    private static String fiksNavNoLinker(String fritekst) {
        return fritekst.replaceAll("(.??)(nav.no/\\w*)(\s*|\\.)(.??)", "$1[$2](https://$2)$3$4");
    }
}

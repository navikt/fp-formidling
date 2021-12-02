package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;

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
        return new Fritekst(ivaretaLinjeskiftIFritekst(konverterOverskrifterTilDokgenFormat(fritekst)));
    }

    //TODO(JEJ): Slettes når alle brev er flyttet til Dokgen
    @Deprecated
    public static Optional<String> forDokprod(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (dokumentHendelse.getFritekst() != null && !dokumentHendelse.getFritekst().isEmpty()) {
            return Optional.of(dokumentHendelse.getFritekst());
        } else if (behandling.getBehandlingsresultat() != null) {
            return Optional.ofNullable(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        }
        return Optional.empty();
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

    // Enkelt-linjeskift blir ignorert. Metoden sørger for at de kommer med ved å legge til ekstra, uten å ødelegge for punktlister:
    static String ivaretaLinjeskiftIFritekst(String fritekst) {
        if (fritekst == null || fritekst.equals("")) {
            return fritekst;
        }
        String[] linjer = fritekst.split("\n");
        if (linjer.length <= 1) {
            return fritekst;
        }

        StringBuilder resultat = new StringBuilder();
        resultat.append(linjer[0]);
        boolean sisteVarPunktliste = false;
        for (int i = 1; i < linjer.length; i++) {
            resultat.append("\n");
            String linje = linjer[i];
            if (linje.startsWith("- ")) {
                // Punktliste
                resultat.append(linje);
                sisteVarPunktliste = true;
            } else if (!linjer[i - 1].startsWith("- ") && !linjer[i - 1].endsWith(".")) {
                // Vi er midt i en setning, antagelig copy-paste fra PDF - legger ikke til linjeskift
                resultat.append(linje);
                sisteVarPunktliste = false;
            } else {
                // Ekstra linjeskift må inn for at det ikke skal ignoreres i Dokgen...
                resultat.append("\n");
                resultat.append(linje);
                sisteVarPunktliste = false;
            }
        }
        if (!sisteVarPunktliste) {
            resultat.append("\n"); // Uten ekstra linjeskift her kommer neste overskrift for nærme...
        }
        return resultat.toString();
    }
}

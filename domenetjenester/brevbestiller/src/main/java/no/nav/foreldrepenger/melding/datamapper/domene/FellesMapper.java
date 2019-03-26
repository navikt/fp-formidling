package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Set;

import no.nav.vedtak.util.StringUtils;

public class FellesMapper {


    public static int formaterLovhjemler(Set<String> hjemler, StringBuilder builder,
                                         String startTillegg, String sluttTillegg) {
        builder.append("§ ");
        int antall = 0;
        for (String hjemmel : hjemler) {
            if (hjemmel.trim().isEmpty()) {
                continue;
            } else if (antall > 0) {
                builder.append(", ");
            }
            builder.append(hjemmel.trim());
            antall++;
        }

        leggTilEkstraSeksjonstegnHvisRelevant(builder, antall);
        settInnEventuelleTillegg(builder, startTillegg, sluttTillegg, antall);

        if (antall > 1 && StringUtils.nullOrEmpty(sluttTillegg)) {
            // bytt ut siste kommaforekomst med " og ".
            int pos = builder.lastIndexOf(",");
            builder.replace(pos, pos + 2, " og ");
        }
        return antall;
    }

    private static void leggTilEkstraSeksjonstegnHvisRelevant(StringBuilder builder, int antall) {
        if (antall > 1) {
            //legg til ekstra § i starten hvis det er mer enn en
            builder.insert(0, "§");
        }
    }

    private static void settInnEventuelleTillegg(StringBuilder builder, String startTillegg, String sluttTillegg, int antall) {
        if (!StringUtils.nullOrEmpty(startTillegg)) {
            builder.insert(0, startTillegg.concat(" "));
        }
        if (!StringUtils.nullOrEmpty(sluttTillegg)) {
            builder.append(antall > 0 ? " og " : "").append(sluttTillegg);
        }
    }

}

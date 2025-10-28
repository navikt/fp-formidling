package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningHjemmel;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;

public class FellesMapper {

    private static final Logger LOG = LoggerFactory.getLogger(FellesMapper.class);

    private FellesMapper() {
    }

    public static List<VilkårType> vilkårFraAvslagsårsak(FagsakYtelseType ytelseType,
                                                         Collection<VilkårType> vilkårFraBehandling,
                                                         Avslagsårsak avslagsårsak) {
        var vilkårTyperMedAvslagsårsak = VilkårType.getVilkårTyper(avslagsårsak);
        return vilkårFraBehandling.stream()
            .filter(vilkårTyperMedAvslagsårsak::contains)
            .filter(vt -> vt.getLovReferanse(ytelseType) != null)
            .toList();
    }

    public static Collection<String> lovhjemmelFraAvslagsårsak(FagsakYtelseType ytelseType,
                                                             Collection<VilkårType> vilkårFraBehandling,
                                                             Avslagsårsak avslagsårsak) {
        var vilkårTyperMedAvslagsårsak = VilkårType.getVilkårTyper(avslagsårsak);
        return vilkårFraBehandling.stream()
            .filter(vilkårTyperMedAvslagsårsak::contains)
            .map(vt -> vt.getLovReferanse(ytelseType))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public static String formaterLovhjemlerForBeregning(String lovhjemmelBeregning,
                                                        String konsekvensForYtelse,
                                                        boolean innvilgetRevurdering,
                                                        UUID behandlingUuid) {
        if (lovhjemmelBeregning == null) {
            lovhjemmelBeregning = "";
        } else if (BeregningHjemmel.UDEFINERT.getLovRef().equals(lovhjemmelBeregning) && !KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()
            .equals(konsekvensForYtelse)) {
            LOG.warn("Behandling {} har udefinert hjemmel. Fint om du sjekker på TFP-4569 om dette er en NY sak, "
                + "og i så fall melder det der så vi kan se hvor ofte det skjer.", behandlingUuid);
            lovhjemmelBeregning = "";
        }
        if (endringIBeregning(konsekvensForYtelse) || innvilgetRevurdering) {
            lovhjemmelBeregning += (" og forvaltningsloven § 35");
        }
        return lovhjemmelBeregning.replace("folketrygdloven ", "");
    }

    public static String formaterLovhjemlerUttak(Set<String> hjemler) {
        return formaterLovhjemlerUttak(hjemler, null, false);
    }

    public static String formaterLovhjemlerUttak(Set<String> hjemler, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        var lovHjemmelBuilder = new StringBuilder();
        var forvaltningslovenTillegg = endringIBeregningEllerInnvilgetRevurdering(innvilgetRevurdering,
            konsekvensForYtelse) ? "forvaltningsloven § 35" : null;
        var antallLovreferanser = formaterLovhjemler(hjemler, lovHjemmelBuilder, null, forvaltningslovenTillegg);
        if (antallLovreferanser == 0 && forvaltningslovenTillegg == null) {
            return "";
        }
        return lovHjemmelBuilder.toString();
    }

    private static boolean endringIBeregningEllerInnvilgetRevurdering(boolean innvilgetRevurdering, String konsekvensForYtelse) {
        return endringIBeregning(konsekvensForYtelse) || innvilgetRevurdering;
    }

    private static boolean endringIBeregning(String konsekvensForYtelse) {
        return KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode().equals(konsekvensForYtelse) || BehandlingMapper.ENDRING_BEREGNING_OG_UTTAK.equals(
            konsekvensForYtelse);
    }

    public static int formaterLovhjemler(Set<String> hjemler, StringBuilder builder, String startTillegg, String sluttTillegg) {
        var antall = 0;
        for (var hjemmel : hjemler) {
            if (hjemmel.trim().isEmpty()) {
                continue;
            } else if (antall > 0) {
                builder.append(", ");
            }
            if (antall == 0 && !hjemmel.contains("§")) {
                builder.append("§ ");
            }
            builder.append(hjemmel.trim());
            antall++;
        }

        leggTilEkstraSeksjonstegnHvisRelevant(builder, antall);
        settInnEventuelleTillegg(builder, startTillegg, sluttTillegg, antall);

        if (antall > 1 && (sluttTillegg == null || sluttTillegg.isEmpty())) {
            // bytt ut siste kommaforekomst med " og ".
            var pos = builder.lastIndexOf(",");
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
        if (startTillegg != null && !startTillegg.isEmpty()) {
            builder.insert(0, startTillegg.concat(" "));
        }
        if (sluttTillegg != null && !sluttTillegg.isEmpty()) {
            builder.append(antall > 0 ? " og " : "").append(sluttTillegg);
        }
    }

}

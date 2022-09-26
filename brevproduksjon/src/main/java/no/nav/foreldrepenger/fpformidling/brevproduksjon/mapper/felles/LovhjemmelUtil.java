package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.fpformidling.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.vedtak.exception.TekniskException;

public class LovhjemmelUtil {

    private LovhjemmelUtil() {

    }

    public static Set<String> hentLovhjemlerFraJson(ÅrsakMedLovReferanse årsak, String key) {
        var jsonData = parseLovDataFor(årsak);
        if (jsonData != null) {
            var hjemmelNode = jsonData.findValue(key);
            if (hjemmelNode != null) {
                var hjemmelListe = !hjemmelNode.findValues("lovreferanse").isEmpty() ? hjemmelNode.findValues("lovreferanse")
                        : hjemmelNode.findValues("lovreferanser");
                return hjemmelListe.stream()
                        .flatMap(node -> node.isContainerNode() ? StreamSupport.stream(node.spliterator(), false).map(JsonNode::asText)
                                : Arrays.stream(node.asText().split(",")))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .collect(Collectors.toCollection(() -> new TreeSet<>(new LovhjemmelComparator())));
            }
        }
        return Set.of();
    }

    public static Set<String> hentLovhjemlerFraJson(FagsakYtelseType fagsakYtelseType, ÅrsakMedLovReferanse årsak) {
        if (fagsakYtelseType.gjelderEngangsstønad()) {
            return hentLovhjemlerFraJson(årsak, "ES");
        } else if (fagsakYtelseType.gjelderForeldrepenger()) {
            return hentLovhjemlerFraJson(årsak, "FP");
        }
        throw new TekniskException("FPFORMIDLING-693339",
                String.format("Mangler informasjon om lovhjemmel for avslagsårsak med kode %s.", årsak.getKode()));
    }

    private static JsonNode parseLovDataFor(ÅrsakMedLovReferanse årsakKode) {
        var lovData = årsakKode.getLovHjemmelData();
        if (lovData == null) {
            return null;
        }
        var objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(lovData);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Ugyldig format (forventet JSON) for kode=" + årsakKode.getKode() //$NON-NLS-1$ //$NON-NLS-2$
                            + " " + lovData, //$NON-NLS-1$
                    e); //$NON-NLS-2$
        }
        return jsonNode;
    }
}

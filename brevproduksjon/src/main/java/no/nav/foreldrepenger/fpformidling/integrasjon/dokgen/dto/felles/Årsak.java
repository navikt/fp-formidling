package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;

public class Årsak {
    private static final List<List<String>> NON_EQUAL_KODER_SOM_LIKEVEL_OPPFYLLER_MERGE = List.of(
        List.of("2001", "2002", "2003", "2004", "2005", "2006", "2007", "2036", "2037"), List.of("2010", "2015"), List.of("2011", "2016"),
        List.of("2012", "2018", "2025"), List.of("2013", "2019", "2026"), List.of("2014", "2017", "2027"), List.of("2030", "2031"),
        List.of("4003", "4012"), List.of("4030", "4031"), List.of("4038", "4110"), List.of("4039", "4111"), List.of("4040", "4112"),
        List.of("4058", "4059"), List.of("4061", "4062", "4063", "4064", "4065", "4115", "4116", "4117"),
        List.of("4066", "4067", "4068", "4069", "4070", "4088", "4089"), List.of("4080", "4501"), List.of("4084", "4085"), List.of("4094", "4502"));

    private static final List<String> UTBETALING_ÅRSAKER = List.of("2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019",
        "2020", "2021", "2022", "2023", "2030", "2031", "2032", "2033", "2034", "2035", "2038");

    private static final List<String> GYLDIG_UTSETTELSE_ÅRSAKER = List.of("2010", "2011", "2012", "2013", "2014");

    @JsonValue
    private String kode;

    private Årsak(String kode) {
        if (kode == null || "".equals(kode)) {
            throw new IllegalStateException("Ugyldig årsak: " + (kode == null ? "null" : "tom string"));
        }
        this.kode = kode;
    }

    public static Årsak of(String kode) {
        return new Årsak(kode);
    }

    public String getKode() {
        return kode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (Årsak) object;
        return Objects.equals(kode, that.kode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode);
    }

    @Override
    public String toString() {
        return "Årsak{" + "kode=" + kode + '}';
    }

    public static boolean erRegnetSomLike(Årsak årsak1, Årsak årsak2) {
        return NON_EQUAL_KODER_SOM_LIKEVEL_OPPFYLLER_MERGE.stream().anyMatch(k -> k.containsAll(Set.of(årsak1.getKode(), årsak2.getKode())));
    }

    public boolean erUtbetalingÅrsak() {
        return UTBETALING_ÅRSAKER.contains(kode);
    }

    public boolean erGyldigUtsettelseÅrsak() {
        return GYLDIG_UTSETTELSE_ÅRSAKER.contains(kode);
    }
}

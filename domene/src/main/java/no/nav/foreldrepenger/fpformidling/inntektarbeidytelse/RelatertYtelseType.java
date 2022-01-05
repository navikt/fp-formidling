package no.nav.foreldrepenger.fpformidling.inntektarbeidytelse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum RelatertYtelseType implements Kodeverdi {

    ENSLIG_FORSØRGER("ENSLIG_FORSØRGER"),
    SYKEPENGER("SYKEPENGER"),
    SVANGERSKAPSPENGER("SVANGERSKAPSPENGER"),
    FORELDREPENGER("FORELDREPENGER"),
    ENGANGSSTØNAD("ENGANGSSTØNAD"),
    PÅRØRENDESYKDOM("PÅRØRENDESYKDOM"),
    FRISINN("FRISINN"),
    PLEIEPENGER_SYKT_BARN("PSB"),
    PLEIEPENGER_NÆRSTÅENDE("PPN"),
    OMSORGSPENGER("OMP"),
    OPPLÆRINGSPENGER("OLP"),
    ARBEIDSAVKLARINGSPENGER("ARBEIDSAVKLARINGSPENGER"),
    DAGPENGER("DAGPENGER"),
    UDEFINERT("-"),
    ;

    private static final Map<String, RelatertYtelseType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "RELATERT_YTELSE_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private RelatertYtelseType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static RelatertYtelseType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent RelatertYtelseType: " + kode);
        }
        return ad;
    }

    public static Map<String, RelatertYtelseType> kodeMap() {
        return Collections.unmodifiableMap(KODER);
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

}

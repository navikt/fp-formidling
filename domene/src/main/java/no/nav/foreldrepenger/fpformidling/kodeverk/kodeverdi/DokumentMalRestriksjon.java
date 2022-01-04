package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum DokumentMalRestriksjon implements Kodeverdi{
    INGEN("INGEN"),
    REVURDERING("REVURDERING"),
    ÅPEN_BEHANDLING("ÅPEN_BEHANDLING"),
    ÅPEN_BEHANDLING_IKKE_SENDT("ÅPEN_BEHANDLING_IKKE_SENDT"),

    // La stå
    UDEFINERT("-"),
    ;
    public static final String KODEVERK = "DOKUMENT_MAL_RESTRIKSJON"; //$NON-NLS-1$

    private static final Map<String, DokumentMalRestriksjon> KODER = new LinkedHashMap<>();


    private String kode;


    private DokumentMalRestriksjon(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static DokumentMalRestriksjon fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent DokumentMalRestriksjon: " + kode);
        }
        return ad;
    }

    public static DokumentMalRestriksjon fraKodeDefaultUdefinert(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        return KODER.getOrDefault(kode, UDEFINERT);
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

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }
}

package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum DokumentKategori implements Kodeverdi {

    UDEFINERT("-"),
    KLAGE_ELLER_ANKE("KLGA"),
    SØKNAD("SOKN"),
    ;

    public static final String KODEVERK = "DOKUMENT_KATEGORI";

    private static final Map<String, DokumentKategori> KODER = new LinkedHashMap<>();

    private String kode;

    DokumentKategori(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static DokumentKategori fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        return KODER.getOrDefault(kode, DokumentKategori.UDEFINERT);
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

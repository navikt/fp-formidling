package no.nav.foreldrepenger.fpformidling.domene.vedtak;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Vedtaksbrev implements Kodeverdi {

    AUTOMATISK("AUTOMATISK"),
    FRITEKST("FRITEKST"),
    INGEN("INGEN"),
    UDEFINERT("-"),
    ;

    private static final Map<String, Vedtaksbrev> KODER = new LinkedHashMap<>();


    @JsonValue
    private String kode;

    Vedtaksbrev(String kode) {
        this.kode = kode;
    }

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

package no.nav.foreldrepenger.fpformidling.klage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum KlageVurdering implements Kodeverdi {
    MEDHOLD_I_KLAGE("MEDHOLD_I_KLAGE"),
    AVVIS_KLAGE("AVVIS_KLAGE"),

    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    KlageVurdering(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


}

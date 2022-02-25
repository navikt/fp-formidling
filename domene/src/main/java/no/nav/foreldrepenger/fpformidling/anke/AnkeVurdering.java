package no.nav.foreldrepenger.fpformidling.anke;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum AnkeVurdering implements Kodeverdi {

    ANKE_STADFESTE_YTELSESVEDTAK("ANKE_STADFESTE_YTELSESVEDTAK"),
    ANKE_OPPHEVE_OG_HJEMSENDE("ANKE_OPPHEVE_OG_HJEMSENDE"),
    ANKE_HJEMSEND_UTEN_OPPHEV("ANKE_HJEMSENDE_UTEN_OPPHEV"),
    ANKE_OMGJOER("ANKE_OMGJOER"),
    ANKE_AVVIS("ANKE_AVVIS"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    AnkeVurdering(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

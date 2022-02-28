package no.nav.foreldrepenger.fpformidling.anke;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum AnkeVurderingOmgjør implements Kodeverdi {

    ANKE_TIL_GUNST("ANKE_TIL_GUNST"),
    ANKE_DELVIS_OMGJOERING_TIL_GUNST("ANKE_DELVIS_OMGJOERING_TIL_GUNST"),
    ANKE_TIL_UGUNST("ANKE_TIL_UGUNST"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private AnkeVurderingOmgjør(String kode) {
        this.kode = kode;
    }


    @Override
    public String getKode() {
        return kode;
    }
}


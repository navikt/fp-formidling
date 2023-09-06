package no.nav.foreldrepenger.fpformidling.uttak.fp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum StønadskontoType implements Kodeverdi {

    FELLESPERIODE("FELLESPERIODE"),
    MØDREKVOTE("MØDREKVOTE"),
    FEDREKVOTE("FEDREKVOTE"),
    FORELDREPENGER("FORELDREPENGER"),
    FORELDREPENGER_FØR_FØDSEL("FORELDREPENGER_FØR_FØDSEL"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private StønadskontoType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

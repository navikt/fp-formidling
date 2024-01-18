package no.nav.foreldrepenger.fpformidling.domene.dokumentdata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum DokumentKategori implements Kodeverdi {

    @JsonEnumDefaultValue UDEFINERT("-"),
    KLAGE_ELLER_ANKE("KLGA"),
    SØKNAD("SOKN"),
    ;

    @JsonValue
    private String kode;

    DokumentKategori(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


}

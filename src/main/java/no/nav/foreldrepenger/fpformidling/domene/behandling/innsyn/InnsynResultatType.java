package no.nav.foreldrepenger.fpformidling.domene.behandling.innsyn;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum InnsynResultatType implements Kodeverdi {

    INNVILGET("INNV"),
    DELVIS_INNVILGET("DELV"),
    AVVIST("AVVIST"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private InnsynResultatType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


}

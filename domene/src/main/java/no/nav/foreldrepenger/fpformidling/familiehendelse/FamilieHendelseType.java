package no.nav.foreldrepenger.fpformidling.familiehendelse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum FamilieHendelseType implements Kodeverdi {

    ADOPSJON("ADPSJN"),
    OMSORG("OMSRGO"),
    FØDSEL("FODSL"),
    TERMIN("TERM"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private FamilieHendelseType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

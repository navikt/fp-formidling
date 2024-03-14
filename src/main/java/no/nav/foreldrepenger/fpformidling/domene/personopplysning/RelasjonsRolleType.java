package no.nav.foreldrepenger.fpformidling.domene.personopplysning;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum RelasjonsRolleType implements Kodeverdi {

    EKTE("EKTE"),
    BARN("BARN"),
    FARA("FARA"),
    MORA("MORA"),
    REGISTRERT_PARTNER("REPA"),
    SAMBOER("SAMB"),
    MEDMOR("MMOR"),
    @JsonEnumDefaultValue UDEFINERT("-"),
    ;

    private static final Set<RelasjonsRolleType> FORELDRE_ROLLER = Set.of(RelasjonsRolleType.MORA, RelasjonsRolleType.FARA,
        RelasjonsRolleType.MEDMOR);

    @JsonValue
    private String kode;

    private RelasjonsRolleType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


    public static boolean erRegistrertForeldre(RelasjonsRolleType type) {
        return FORELDRE_ROLLER.contains(type);
    }

}

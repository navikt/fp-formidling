package no.nav.foreldrepenger.fpformidling.domene.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum KonsekvensForYtelsen implements Kodeverdi {


    FORELDREPENGER_OPPHØRER("FORELDREPENGER_OPPHØRER"),
    ENDRING_I_BEREGNING("ENDRING_I_BEREGNING"),
    ENDRING_I_UTTAK("ENDRING_I_UTTAK"),
    ENDRING_I_FORDELING_AV_YTELSEN("ENDRING_I_FORDELING_AV_YTELSEN"),
    INGEN_ENDRING("INGEN_ENDRING"),
    ENDRING_I_BEREGNING_OG_UTTAK("ENDRING_I_BEREGNING_OG_UTTAK"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private KonsekvensForYtelsen(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


}

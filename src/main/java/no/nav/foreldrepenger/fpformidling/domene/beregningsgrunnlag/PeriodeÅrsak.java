package no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum PeriodeÅrsak implements Kodeverdi {

    NATURALYTELSE_BORTFALT("NATURALYTELSE_BORTFALT"),
    ARBEIDSFORHOLD_AVSLUTTET("ARBEIDSFORHOLD_AVSLUTTET"),
    NATURALYTELSE_TILKOMMER("NATURALYTELSE_TILKOMMER"),
    ENDRING_I_REFUSJONSKRAV("ENDRING_I_REFUSJONSKRAV"),
    REFUSJON_OPPHØRER("REFUSJON_OPPHØRER"),
    GRADERING("GRADERING"),
    GRADERING_OPPHØRER("GRADERING_OPPHØRER"),
    ENDRING_I_AKTIVITETER_SØKT_FOR("ENDRING_I_AKTIVITETER_SØKT_FOR"),
    REFUSJON_AVSLÅTT("REFUSJON_AVSLÅTT"),

    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    private PeriodeÅrsak(String kode) {
        this.kode = kode;
    }


    @Override
    public String getKode() {
        return kode;
    }

}

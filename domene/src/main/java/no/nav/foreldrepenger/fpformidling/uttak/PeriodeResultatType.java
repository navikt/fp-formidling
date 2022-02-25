package no.nav.foreldrepenger.fpformidling.uttak;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum PeriodeResultatType implements Kodeverdi {

    INNVILGET("INNVILGET"),
    AVSLÅTT("AVSLÅTT"),
    IKKE_FASTSATT("IKKE_FASTSATT"),
    MANUELL_BEHANDLING("MANUELL_BEHANDLING"),

    /** @deprecated kan fjernes når beregning har sluttet å bruke. */
    @Deprecated
    GYLDIG_UTSETTELSE("GYLDIG_UTSETTELSE"),

    /** @deprecated kan fjernes når beregning har sluttet å bruke. */
    @Deprecated
    UGYLDIG_UTSETTELSE("UGYLDIG_UTSETTELSE"),
    ;

    @JsonValue
    private String kode;

    private PeriodeResultatType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

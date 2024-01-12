package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum PeriodeResultatType implements Kodeverdi {

    INNVILGET("INNVILGET"),
    AVSLÅTT("AVSLÅTT"),
    IKKE_FASTSATT("IKKE_FASTSATT"),
    MANUELL_BEHANDLING("MANUELL_BEHANDLING"),
    ;

    @JsonValue
    private String kode;

    PeriodeResultatType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

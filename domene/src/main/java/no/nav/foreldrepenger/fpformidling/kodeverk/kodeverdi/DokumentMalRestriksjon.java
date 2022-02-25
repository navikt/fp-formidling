package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum DokumentMalRestriksjon implements Kodeverdi{
    INGEN("INGEN"),
    REVURDERING("REVURDERING"),
    ÅPEN_BEHANDLING("ÅPEN_BEHANDLING"),
    ÅPEN_BEHANDLING_IKKE_SENDT("ÅPEN_BEHANDLING_IKKE_SENDT"),

    // La stå
    UDEFINERT("-"),
    ;
    @JsonValue
    private String kode;

    private DokumentMalRestriksjon(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

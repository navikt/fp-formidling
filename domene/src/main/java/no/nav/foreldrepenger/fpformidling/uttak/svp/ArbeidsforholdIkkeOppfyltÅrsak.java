package no.nav.foreldrepenger.fpformidling.uttak.svp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum ArbeidsforholdIkkeOppfyltÅrsak implements Kodeverdi {

    INGEN("-" ),
    HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO("8301"),
    UTTAK_KUN_PÅ_HELG("8302"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE("8303"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN("8312"),
    ;

    @JsonValue
    private String kode;

    private ArbeidsforholdIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.KodeverdiMedNavn;

public enum Hjemmel implements Kodeverdi, KodeverdiMedNavn {

    F_14_7("F_14_7", "folketrygdloven § 14-7"),
    F_14_7_8_30("F_14_7_8_30", "folketrygdloven §§ 14-7 og 8-30"),
    F_14_7_8_28_8_30("F_14_7_8_28_8_30", "folketrygdloven §§ 14-7, 8-28 og 8-30"),
    F_14_7_8_35("F_14_7_8_35", "folketrygdloven §§ 14-7 og 8-35"),
    F_14_7_8_38("F_14_7_8_38", "folketrygdloven §§ 14-7 og 8-38"),
    F_14_7_8_40("F_14_7_8_40", "folketrygdloven §§ 14-7 og 8-40"),
    F_14_7_8_41("F_14_7_8_41", "folketrygdloven §§ 14-7 og 8-41"),
    F_14_7_8_42("F_14_7_8_42", "folketrygdloven §§ 14-7 og 8-42"),
    F_14_7_8_43("F_14_7_8_43", "folketrygdloven §§ 14-7 og 8-43"),
    F_14_7_8_47("F_14_7_8_47", "folketrygdloven §§ 14-7 og 8-47"),
    F_14_7_8_49("F_14_7_8_49", "folketrygdloven §§ 14-7 og 8-49"),
    UDEFINERT("-", "Ikke definert"),
    ;

    private String navn;

    private String kode;

    Hjemmel(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @Override
    public String getNavn() {
        return navn;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

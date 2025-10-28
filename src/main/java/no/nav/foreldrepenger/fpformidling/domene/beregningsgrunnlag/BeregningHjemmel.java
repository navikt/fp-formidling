package no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag;

public enum BeregningHjemmel {

    F_14_7("folketrygdloven § 14-7"),
    F_14_7_8_30("folketrygdloven §§ 14-7 og 8-30"),
    F_14_7_8_28_8_30("folketrygdloven §§ 14-7, 8-28 og 8-30"),
    F_14_7_8_35("folketrygdloven §§ 14-7 og 8-35"),
    F_14_7_8_38("folketrygdloven §§ 14-7 og 8-38"),
    F_14_7_8_40("folketrygdloven §§ 14-7 og 8-40"),
    F_14_7_8_41("folketrygdloven §§ 14-7 og 8-41"),
    F_14_7_8_42("folketrygdloven §§ 14-7 og 8-42"),
    F_14_7_8_43("folketrygdloven §§ 14-7 og 8-43"),
    F_14_7_8_47("folketrygdloven §§ 14-7 og 8-47"),
    F_14_7_8_49("folketrygdloven §§ 14-7 og 8-49"),
    UDEFINERT("Ikke definert"),
    ;

    private final String lovRef;

    BeregningHjemmel(String lovRef) {
        this.lovRef = lovRef;
    }

    public String getLovRef() {
        return lovRef;
    }

}

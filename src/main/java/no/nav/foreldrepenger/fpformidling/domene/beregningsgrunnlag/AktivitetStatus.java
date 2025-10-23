package no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag;

import java.util.Set;

public enum AktivitetStatus {
    ARBEIDSAVKLARINGSPENGER,
    ARBEIDSTAKER,
    DAGPENGER,
    FRILANSER,
    MILITÆR_ELLER_SIVIL,
    SELVSTENDIG_NÆRINGSDRIVENDE,
    KOMBINERT_AT_FL,
    KOMBINERT_AT_SN,
    KOMBINERT_FL_SN,
    KOMBINERT_AT_FL_SN,
    BRUKERS_ANDEL,
    KUN_YTELSE,
    TTLSTØTENDE_YTELSE,
    VENTELØNN_VARTPENGER;

    private static final Set<AktivitetStatus> KOMBINERTE_STATUSER = Set.of(KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL, KOMBINERT_AT_SN, KOMBINERT_FL_SN);

    public boolean erKombinertStatus() {
        return KOMBINERTE_STATUSER.contains(this);
    }

}

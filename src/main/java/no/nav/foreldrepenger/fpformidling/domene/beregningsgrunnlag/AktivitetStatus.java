package no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum AktivitetStatus implements Kodeverdi {
    ARBEIDSAVKLARINGSPENGER("AAP"),
    ARBEIDSTAKER("AT"),
    DAGPENGER("DP"),
    FRILANSER("FL"),
    MILITÆR_ELLER_SIVIL("MS"),
    SELVSTENDIG_NÆRINGSDRIVENDE("SN"),
    KOMBINERT_AT_FL("AT_FL"),
    KOMBINERT_AT_SN("AT_SN"),
    KOMBINERT_FL_SN("FL_SN"),
    KOMBINERT_AT_FL_SN("AT_FL_SN"),
    BRUKERS_ANDEL("BA"),
    KUN_YTELSE("KUN_YTELSE"),

    TTLSTØTENDE_YTELSE("TY"),
    VENTELØNN_VARTPENGER("VENTELØNN_VARTPENGER"),

    UDEFINERT("-");

    @JsonValue
    private String kode;

    AktivitetStatus(String kode) {
        this.kode = kode;
    }

    private static final Set<AktivitetStatus> AT_STATUSER = Set.of(ARBEIDSTAKER, KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_AT_FL);

    private static final Set<AktivitetStatus> SN_STATUSER = Set.of(SELVSTENDIG_NÆRINGSDRIVENDE, KOMBINERT_AT_FL_SN, KOMBINERT_AT_SN, KOMBINERT_FL_SN);

    private static final Set<AktivitetStatus> FL_STATUSER = Set.of(FRILANSER, KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL, KOMBINERT_FL_SN);

    private static final Set<AktivitetStatus> KOMBINERTE_STATUSER = Set.of(KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL, KOMBINERT_AT_SN, KOMBINERT_FL_SN);

    public boolean erArbeidstaker() {
        return AT_STATUSER.contains(this);
    }

    public boolean erSelvstendigNæringsdrivende() {
        return SN_STATUSER.contains(this);
    }

    public boolean erFrilanser() {
        return FL_STATUSER.contains(this);
    }

    public boolean harKombinertStatus() {
        return KOMBINERTE_STATUSER.contains(this);
    }

    public static boolean erKombinertStatus(String aktivitetStatus) {
        return KOMBINERTE_STATUSER.stream().map(AktivitetStatus::name).anyMatch(as -> as.equals(aktivitetStatus));
    }

    @Override
    public String getKode() {
        return kode;
    }

}

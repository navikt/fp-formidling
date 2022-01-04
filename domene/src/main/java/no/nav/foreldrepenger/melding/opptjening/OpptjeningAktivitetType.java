package no.nav.foreldrepenger.melding.opptjening;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum OpptjeningAktivitetType implements Kodeverdi {

    ARBEIDSAVKLARING("AAP"),
    ARBEID("ARBEID"),
    DAGPENGER("DAGPENGER"),
    FORELDREPENGER("FORELDREPENGER"),
    FRILANS("FRILANS"),
    MILITÆR_ELLER_SIVILTJENESTE("MILITÆR_ELLER_SIVILTJENESTE"),
    NÆRING("NÆRING"),
    OMSORGSPENGER("OMSORGSPENGER"),
    OPPLÆRINGSPENGER("OPPLÆRINGSPENGER"),
    PLEIEPENGER("PLEIEPENGER"),
    FRISINN("FRISINN"),
    ETTERLØNN_SLUTTPAKKE("ETTERLØNN_SLUTTPAKKE"),
    SVANGERSKAPSPENGER("SVANGERSKAPSPENGER"),
    SYKEPENGER("SYKEPENGER"),
    VENTELØNN_VARTPENGER("VENTELØNN_VARTPENGER"),
    VIDERE_ETTERUTDANNING("VIDERE_ETTERUTDANNING"),
    UTENLANDSK_ARBEIDSFORHOLD("UTENLANDSK_ARBEIDSFORHOLD"),
    UTDANNINGSPERMISJON("UTDANNINGSPERMISJON"),
    UDEFINERT("-"),
            ;

    private static final Map<String, OpptjeningAktivitetType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "OPPTJENING_AKTIVITET_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private OpptjeningAktivitetType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static OpptjeningAktivitetType fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent OpptjeningAktivitetType: " + kode);
        }
        return ad;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }


}

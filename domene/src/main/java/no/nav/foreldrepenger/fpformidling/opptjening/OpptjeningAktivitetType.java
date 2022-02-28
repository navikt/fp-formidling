package no.nav.foreldrepenger.fpformidling.opptjening;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


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
    @JsonEnumDefaultValue
    UDEFINERT("-"),
            ;

    @JsonValue
    private String kode;

    private OpptjeningAktivitetType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }


}

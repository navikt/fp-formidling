package no.nav.foreldrepenger.melding.uttak.svp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum PeriodeIkkeOppfyltÅrsak implements Kodeverdi, ÅrsakMedLovReferanse {

    INGEN("-"),


    BARNET_ER_DØD("4072"),
    SØKER_ER_DØD("4071"),
    OPPHØR_MEDLEMSKAP("4087"),
    SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT("4098"),
    OPPTJENINGSVILKÅRET_IKKE_OPPFYLT("4099"),
    _8304("8304"),
    _8305("8305"),
    _8306("8306"),
    SØKT_FOR_SENT("8308"),
    _8309("8309"),
    _8310("8310"),
    PERIODE_SAMTIDIG_SOM_FERIE("8311"),
    _8313("8313"),

    ;

    private static final Map<String, PeriodeIkkeOppfyltÅrsak> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "SVP_PERIODE_IKKE_OPPFYLT_AARSAK";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    PeriodeIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static PeriodeIkkeOppfyltÅrsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent PeriodeIkkeOppfyltÅrsak: " + kode);
        }
        return ad;
    }

    public static Map<String, PeriodeIkkeOppfyltÅrsak> kodeMap() {
        return Collections.unmodifiableMap(KODER);
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

    @Override
    public String getLovHjemmelData() {
        return null;
    }

}


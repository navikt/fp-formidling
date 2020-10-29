package no.nav.foreldrepenger.melding.uttak.svp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum PeriodeIkkeOppfyltÅrsak implements Kodeverdi, ÅrsakMedLovReferanse {

    INGEN("-", null),

    BARNET_ER_DØD("4072", "{\"fagsakYtelseType\": {\"SVP\": {\"lovreferanse\": \"14-9\"}}}"),
    SØKER_ER_DØD("4071", "{\"fagsakYtelseType\": {\"fagsakYtelseType\": SVP\": {\"lovreferanse\": \"14-10\"}}}"),
    OPPHØR_MEDLEMSKAP("4087", "{\"fagsakYtelseType\": {\"fagsakYtelseType\": SVP\": {\"lovreferanse\": \"14-2\"}}}"),
    SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT("4098", "{\"fagsakYtelseType\": {\"SVP\": {\"lovreferanse\": \"14-5\"}}}"),
    OPPTJENINGSVILKÅRET_IKKE_OPPFYLT("4099", "{\"fagsakYtelseType\": {\"SVP\": {\"lovreferanse\": \"14-6\"}}}"),
    _8304("8304", null),
    _8305("8305",  null),
    _8306("8306", null),
    SØKT_FOR_SENT("8308", null),
    _8309("8309", null),
    _8310("8310", null),
    PERIODE_SAMTIDIG_SOM_FERIE("8311", null),
    _8313("8313", null),

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

    @JsonIgnore
    private String lovHjemmel;

    private PeriodeIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    private PeriodeIkkeOppfyltÅrsak(String kode, String lovHjemmel) {
        this.kode = kode;
        this.lovHjemmel = lovHjemmel;
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
        return lovHjemmel;
    }

    public static Set<PeriodeIkkeOppfyltÅrsak> opphørsAvslagÅrsaker() {
        return new HashSet<>(Arrays.asList(
                _8304,
                _8305,
                _8306,
                _8309));
    }

}


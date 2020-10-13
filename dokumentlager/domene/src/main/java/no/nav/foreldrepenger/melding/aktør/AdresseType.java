package no.nav.foreldrepenger.melding.aktør;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum AdresseType implements Kodeverdi {

    BOSTEDSADRESSE("BOSTEDSADRESSE", "BOAD"),
    POSTADRESSE("POSTADRESSE", "POST"),
    POSTADRESSE_UTLAND("POSTADRESSE_UTLAND", "PUTL"),
    MIDLERTIDIG_POSTADRESSE_NORGE("MIDLERTIDIG_POSTADRESSE_NORGE", "TIAD"),
    MIDLERTIDIG_POSTADRESSE_UTLAND("MIDLERTIDIG_POSTADRESSE_UTLAND", "UTAD"),
    UKJENT_ADRESSE("UKJENT_ADRESSE", "UKJE"),
    ;

    private static final Map<String, AdresseType> KODER = new LinkedHashMap<>();

    private static final String KODEVERK = "ADRESSE_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
            KODER.putIfAbsent(v.offisiellKode, v);
        }
    }

    private String kode;

    @JsonIgnore
    private String offisiellKode;

    private AdresseType(String kode, String offisiellKode) {
        this.kode = kode;
        this.offisiellKode = offisiellKode;
    }

    @JsonCreator
    public static AdresseType fraKode(@JsonProperty("kode") String kode) {
        return fraKodeOptional(kode).orElseThrow(() -> new IllegalArgumentException("Ukjent AdresseType: " + kode));
    }

    public static Optional<AdresseType> fraKodeOptional(String kode) {
        if (kode == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(KODER.get(kode));
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

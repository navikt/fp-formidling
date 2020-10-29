package no.nav.foreldrepenger.melding.personopplysning;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum RelasjonsRolleType implements Kodeverdi {

    EKTE("EKTE"),
    BARN("BARN"),
    FARA("FARA"),
    MORA("MORA"),
    REGISTRERT_PARTNER("REPA"),
    SAMBOER("SAMB"),
    MEDMOR("MMOR"),

    UDEFINERT("-"),
    ;

    private static final Map<String, RelasjonsRolleType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "RELASJONSROLLE_TYPE";

    private static final Set<RelasjonsRolleType> FORELDRE_ROLLER = Set.of(RelasjonsRolleType.MORA, RelasjonsRolleType.FARA, RelasjonsRolleType.MEDMOR);

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private RelasjonsRolleType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static RelasjonsRolleType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = fraKodeOptional(kode);
        if (ad.isEmpty()) {
            throw new IllegalArgumentException("Ukjent RelasjonsRolleType: " + kode);
        }
        return ad.get();
    }

    public static Optional<RelasjonsRolleType> fraKodeOptional(String kode) {
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
    
    public static boolean erFar(RelasjonsRolleType relasjon) {
        return FARA.getKode().equals(relasjon.getKode());
    }

    public static boolean erMedmor(RelasjonsRolleType relasjon) {
        return MEDMOR.getKode().equals(relasjon.getKode());
    }

    public static boolean erFarEllerMedmor(RelasjonsRolleType relasjon) {
        return erFar(relasjon) || erMedmor(relasjon);
    }

    public static boolean erMor(RelasjonsRolleType relasjon) {
        return MORA.getKode().equals(relasjon.getKode());
    }

    public static boolean erRegistrertForeldre(RelasjonsRolleType type) {
        return FORELDRE_ROLLER.contains(type);
    }

}

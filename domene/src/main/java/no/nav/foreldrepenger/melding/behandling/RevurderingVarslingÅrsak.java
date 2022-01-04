package no.nav.foreldrepenger.melding.behandling;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum RevurderingVarslingÅrsak implements Kodeverdi {

    BARN_IKKE_REGISTRERT_FOLKEREGISTER("BARNIKKEREG"),
    ARBEIDS_I_STØNADSPERIODEN("JOBBFULLTID"),
    BEREGNINGSGRUNNLAG_UNDER_HALV_G("IKKEOPPTJENT"),
    BRUKER_REGISTRERT_UTVANDRET("UTVANDRET"),
    ARBEID_I_UTLANDET("JOBBUTLAND"),
    IKKE_LOVLIG_OPPHOLD("IKKEOPPHOLD"),
    OPPTJENING_IKKE_OPPFYLT("JOBB6MND"),
    MOR_AKTIVITET_IKKE_OPPFYLT("AKTIVITET"),
    ANNET("ANNET"),
    UDEFINERT("-"),
    ;

    private static final Map<String, RevurderingVarslingÅrsak> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "REVURDERING_VARSLING_AARSAK";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonIgnore
    private String navn;

    private String kode;

    RevurderingVarslingÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static RevurderingVarslingÅrsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent RevurderingVarslingÅrsak: " + kode);
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

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<RevurderingVarslingÅrsak, String> {
        @Override
        public String convertToDatabaseColumn(RevurderingVarslingÅrsak attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public RevurderingVarslingÅrsak convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }
}

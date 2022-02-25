package no.nav.foreldrepenger.fpformidling.behandling;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;


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

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String navn;

    private String kode;

    RevurderingVarslingÅrsak(String kode) {
        this.kode = kode;
    }

    public static RevurderingVarslingÅrsak fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent RevurderingVarslingÅrsak: " + kode);
        }
        return ad;
    }

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

package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum BehandlingResultatType implements Kodeverdi {

    IKKE_FASTSATT("IKKE_FASTSATT"),
    INNVILGET("INNVILGET"),
    AVSLÅTT("AVSLÅTT"),
    OPPHØR("OPPHØR"),
    HENLAGT_SØKNAD_TRUKKET("HENLAGT_SØKNAD_TRUKKET"),
    HENLAGT_FEILOPPRETTET("HENLAGT_FEILOPPRETTET"),
    HENLAGT_BRUKER_DØD("HENLAGT_BRUKER_DØD"),
    MERGET_OG_HENLAGT("MERGET_OG_HENLAGT"),
    HENLAGT_SØKNAD_MANGLER("HENLAGT_SØKNAD_MANGLER"),
    FORELDREPENGER_ENDRET("FORELDREPENGER_ENDRET"),
    INGEN_ENDRING("INGEN_ENDRING"),
    MANGLER_BEREGNINGSREGLER("MANGLER_BEREGNINGSREGLER"),

    // Klage
    KLAGE_AVVIST("KLAGE_AVVIST"),
    KLAGE_MEDHOLD("KLAGE_MEDHOLD"),
    KLAGE_YTELSESVEDTAK_OPPHEVET("KLAGE_YTELSESVEDTAK_OPPHEVET"),
    KLAGE_YTELSESVEDTAK_STADFESTET("KLAGE_YTELSESVEDTAK_STADFESTET"),
    KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET("KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET"), // Brukes av kun Tilbakekreving eller Tilbakekreving Revurdering
    HENLAGT_KLAGE_TRUKKET("HENLAGT_KLAGE_TRUKKET"),
    DELVIS_MEDHOLD_I_KLAGE("DELVIS_MEDHOLD_I_KLAGE"),
    HJEMSENDE_UTEN_OPPHEVE("HJEMSENDE_UTEN_OPPHEVE"),
    UGUNST_MEDHOLD_I_KLAGE("UGUNST_MEDHOLD_I_KLAGE"),

    // Anke
    ANKE_AVVIST("ANKE_AVVIST"),
    ANKE_OMGJOER("ANKE_OMGJOER"),
    ANKE_OPPHEVE_OG_HJEMSENDE("ANKE_OPPHEVE_OG_HJEMSENDE"),
    ANKE_HJEMSENDE_UTEN_OPPHEV("ANKE_HJEMSENDE_UTEN_OPPHEV"),
    ANKE_YTELSESVEDTAK_STADFESTET("ANKE_YTELSESVEDTAK_STADFESTET"),
    ANKE_DELVIS_OMGJOERING_TIL_GUNST("ANKE_DELVIS_OMGJOERING_TIL_GUNST"),
    ANKE_TIL_UGUNST("ANKE_TIL_UGUNST"),

    // Innsyn
    INNSYN_INNVILGET("INNSYN_INNVILGET"),
    INNSYN_DELVIS_INNVILGET("INNSYN_DELVIS_INNVILGET"),
    INNSYN_AVVIST("INNSYN_AVVIST"),
    HENLAGT_INNSYN_TRUKKET("HENLAGT_INNSYN_TRUKKET"),

    ;

    private static final Set<BehandlingResultatType> ALLE_HENLEGGELSESKODER = Set.of(HENLAGT_SØKNAD_TRUKKET, HENLAGT_FEILOPPRETTET, HENLAGT_BRUKER_DØD, HENLAGT_KLAGE_TRUKKET, MERGET_OG_HENLAGT, HENLAGT_SØKNAD_MANGLER, HENLAGT_INNSYN_TRUKKET, MANGLER_BEREGNINGSREGLER);

    private static final Map<String, BehandlingResultatType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "BEHANDLING_RESULTAT_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    private BehandlingResultatType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static BehandlingResultatType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent BehandlingResultatType: " + kode);
        }
        return ad;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    public static Set<BehandlingResultatType> getAlleHenleggelseskoder() {
        return ALLE_HENLEGGELSESKODER;
    }

    public boolean erHenlagt() {
        return ALLE_HENLEGGELSESKODER.contains(this);
    }
}

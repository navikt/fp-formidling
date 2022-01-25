package no.nav.foreldrepenger.fpsak.dto.kodeverk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KodeDto {
    private String kodeverk;
    private String kode;

    public KodeDto() {
    }

    public KodeDto(String kodeverk, String kode) {
        this.kodeverk = kodeverk;
        this.kode = kode;
    }

    /*
     * Etter at fpsak begynner å serialisere som String isf { "kode": "<kode>", "kodeverk": "<hodeverk>" }
     * Så kan man gjøre om alle KodeDto-instanser til String.
     * Eller enda bedre - rett til enum så man slipper <Enum>.fraKode(kodedto.kode) i alle mappere.
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static KodeDto fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        return TempAvledeKode.getVerdiKodeDto(node, "kode");
    }

    public String getKode() {
        return kode;
    }

    public String getKodeverk() {
        return kodeverk;
    }

    @Override
    public String toString() {
        return "Kode{" +
                "kodeverk='" + kodeverk + '\'' +
                ", kode='" + kode + '\'' +
                '}';
    }
}

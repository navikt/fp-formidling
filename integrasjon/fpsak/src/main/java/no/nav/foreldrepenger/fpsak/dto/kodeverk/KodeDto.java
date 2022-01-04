package no.nav.foreldrepenger.fpsak.dto.kodeverk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

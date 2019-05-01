package no.nav.foreldrepenger.fpsak.dto.kodeverk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KodeDto {
    private String kodeverk;
    private String kode;
    private String navn;

    public KodeDto() {
    }

    public KodeDto(String kodeverk, String kode, String navn) {
        this.kodeverk = kodeverk;
        this.kode = kode;
        this.navn = navn;
    }

    public String getKode() {
        return kode;
    }

    @Override
    public String toString() {
        return "Kode{" +
                "kodeverk='" + kodeverk + '\'' +
                ", kode='" + kode + '\'' +
                ", navn='" + navn + '\'' +
                '}';
    }
}

package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DokumentVariantPDF {
    @JsonProperty("filtype")
    private String filtype = "PDFA";
    @JsonProperty("variantformat")
    private String variantformat = "ARKIV";
    @JsonProperty("fysiskDokument")
    private byte[] fysiskDokument;

    public DokumentVariantPDF(byte[] brev) {
        this.fysiskDokument = brev;
    }

    @Override
    public String toString() {
        return "Dokumentvariant{" +
                "variantFormat=" + variantformat +
                ", filtype='" + filtype + '\'' +
                '}';
    }
    public byte[] getFysiskDokument() {
        return fysiskDokument;
    }

    public String getFiltype() {
        return filtype;
    }

    public String getVariantformat() {
        return variantformat;
    }
}

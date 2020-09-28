package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DokumentVariantPDF {
    @JsonProperty("filtype")
    private JournalpostFiltype filtype = JournalpostFiltype.PDFA;
    @JsonProperty("variantformat")
    private String variantformat = "ARKIV";
    @JsonProperty("fysiskDokument")
    private String fysiskDokument;

    enum JournalpostFiltype {
        PDF, PDFA, XML, RTF, AFP, META, DLF, JPEG, TIFF, DOC, DOCX, XLS, XLSX, AXML, DXML, JSON, PNG
    }
//Må gjøre noe her - Må ha base64Binary - korleis?
    public DokumentVariantPDF(byte[] brev) {
        this.fysiskDokument = Base64.getEncoder().encodeToString(brev);
    }
    @Override
    public String toString() {
        return "Dokumentvariant{" +
                "variantFormat=" + variantformat +
                ", filtype='" + filtype + '\'' +
                '}';
    }
}

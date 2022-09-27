package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DokumentOpprettRequest {
    @JsonProperty("tittel")
    private String tittel;
    @JsonProperty("brevkode")
    private String brevkode;
    @JsonProperty("dokumentKategori")
    private String dokumentKategori;
    @JsonProperty("dokumentvarianter")
    private List<DokumentVariantPDF> dokumentvarianter;

    public DokumentOpprettRequest(String tittel, String brevkode, String dokumentKategori, byte[] brev) {
        var dokumentVariantPDF = new DokumentVariantPDF(brev);
        this.tittel = tittel;
        this.brevkode = brevkode;
        this.dokumentKategori = dokumentKategori;
        this.dokumentvarianter = List.of(dokumentVariantPDF);
    }

    public DokumentOpprettRequest(@JsonProperty("tittel")String tittel,
                                  @JsonProperty("brevkode")String brevkode,
                                  @JsonProperty("dokumentKategori")String dokumentKategori,
                                  @JsonProperty("dokumentvarianter")List<DokumentVariantPDF> dokumentvarianter) {
        this.tittel = tittel;
        this.brevkode = brevkode;
        this.dokumentKategori = dokumentKategori;
        this.dokumentvarianter = dokumentvarianter;
    }

    public String getTittel() {
        return tittel;
    }

    public String getBrevkode() {
        return brevkode;
    }

    public String getdokumentKategori() {
        return dokumentKategori;
    }

    public List<DokumentVariantPDF> getDokumentvarianter() {
        return dokumentvarianter;
    }

    @Override
    public String toString() {
        return "DokumentOpprettRequest{" +
                "tittel='" + tittel + '\'' +
                ", brevkode='" + brevkode + '\'' +
                ", dokumentKategori=" + dokumentKategori +
                ", dokumentvarianter=" + dokumentvarianter +
                '}';
    }
}

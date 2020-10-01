package no.nav.foreldrepenger.melding.integrasjon.journal.dto;

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
    @JsonProperty("dokumentVariantPDF")
    private List<DokumentVariantPDF> dokumentVariantPDF;

    public DokumentOpprettRequest(String tittel, String brevkode, String dokumentKategori, byte[] brev) {
        DokumentVariantPDF dokumentVariantPDF = new DokumentVariantPDF(brev);
        this.tittel = tittel;
        this.brevkode = brevkode;
        this.dokumentKategori = dokumentKategori;
        this.dokumentVariantPDF = List.of(dokumentVariantPDF);
    }

    public DokumentOpprettRequest(@JsonProperty("tittel")String tittel,
                                  @JsonProperty("brevkode")String brevkode,
                                  @JsonProperty("dokumentKategori")String dokumentKategori,
                                  @JsonProperty("dokumentVariantPDF")List<DokumentVariantPDF> dokumentVariantPDF) {
        this.tittel = tittel;
        this.brevkode = brevkode;
        this.dokumentKategori = dokumentKategori;
        this.dokumentVariantPDF = dokumentVariantPDF;
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
        return dokumentVariantPDF;
    }

    @Override
    public String toString() {
        return "DokumentOpprettRequest{" +
                ", tittel='" + tittel + '\'' +
                ", brevkode='" + brevkode + '\'' +
                ", dokumentKategori=" + dokumentKategori +
                ", dokumentVariantPDF=" + dokumentVariantPDF +
                '}';
    }
}

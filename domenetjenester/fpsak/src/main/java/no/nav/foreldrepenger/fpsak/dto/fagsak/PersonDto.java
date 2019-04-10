package no.nav.foreldrepenger.fpsak.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDto {

    @JsonProperty("navn")
    private String navn;

    @JsonProperty("alder")
    private Integer alder;

    @JsonProperty("personnummer")
    private String personnummer;

    @JsonProperty("erKvinne")
    private Boolean erKvinne;

    @JsonProperty("personstatusType")
    private KodeDto personstatusType;

    @JsonProperty("diskresjonskode")
    private String diskresjonskode;

    @JsonProperty("dodsdato")
    private LocalDate dodsdato;

    public PersonDto() {
        // Injiseres i test
    }

    public String getNavn() {
        return navn;
    }

    public Integer getAlder() {
        return alder;
    }

    public String getPersonnummer() {
        return personnummer;
    }

    public Boolean getErKvinne() {
        return erKvinne;
    }

    public KodeDto getPersonstatusType() {
        return personstatusType;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

    public LocalDate getDodsdato() {
        return dodsdato;
    }

}

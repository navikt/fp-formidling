package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AvklartDataFodselDto.class, name = "AvklartDataFodselDto"),
        @JsonSubTypes.Type(value = AvklartDataAdopsjonDto.class, name = "AvklartDataAdopsjonDto"),
        @JsonSubTypes.Type(value = AvklartDataOmsorgDto.class, name = "AvklartDataOmsorgDto")
})
public abstract class FamiliehendelseDto {

    private SøknadType soknadType;
    private LocalDate skjæringstidspunkt;

    public FamiliehendelseDto() {
    }

    public boolean erSoknadsType(SøknadType søknadType) {
        return søknadType.equals(this.soknadType);
    }

    @JsonProperty("skjaringstidspunkt")
    public LocalDate getSkjæringstidspunkt() {
        return skjæringstidspunkt;
    }

    public void setSkjæringstidspunkt(LocalDate skjæringstidspunkt) {
        this.skjæringstidspunkt = skjæringstidspunkt;
    }

}

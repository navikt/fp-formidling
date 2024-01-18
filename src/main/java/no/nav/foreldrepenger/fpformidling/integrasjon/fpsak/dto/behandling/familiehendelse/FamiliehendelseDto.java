package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(value = AvklartDataFodselDto.class), @JsonSubTypes.Type(value = AvklartDataAdopsjonDto.class), @JsonSubTypes.Type(value = AvklartDataOmsorgDto.class)})
public abstract class FamiliehendelseDto {

    private LocalDate skjæringstidspunkt;

    FamiliehendelseDto() {
    }

    @JsonProperty("skjaringstidspunkt")
    public LocalDate getSkjæringstidspunkt() {
        return skjæringstidspunkt;
    }

}

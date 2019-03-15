package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AvklartDataFodselDto.class),
        @JsonSubTypes.Type(value = AvklartDataAdopsjonDto.class),
        @JsonSubTypes.Type(value = AvklartDataOmsorgDto.class)
})
public abstract class FamiliehendelseDto {

    private SøknadType soknadType;
    private LocalDate skjæringstidspunkt;
    private Integer gjeldendeAntallBarn;

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

    public int getGjeldendeAntallBarn() {
        return gjeldendeAntallBarn;
    }

}

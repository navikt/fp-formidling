package no.nav.vedtak.felles.prosesstask.rest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.nav.vedtak.log.sporingslogg.Sporingsdata;
import no.nav.vedtak.log.sporingslogg.StandardSporingsloggId;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.FPDateUtil;

@ApiModel
public class SokeFilterDto implements AbacDto {

    @Size(max = 10)
    @Valid
    private List<ProsessTaskStatusDto> prosessTaskStatuser = new ArrayList<>();
    private LocalDateTime sisteKjoeretidspunktFraOgMed = FPDateUtil.nå().minusHours(24);
    private LocalDateTime sisteKjoeretidspunktTilOgMed = FPDateUtil.nå();

    public SokeFilterDto() {
    }

    @ApiModelProperty(value = "Angi liste over prosesstask-statuser som skal søkes på, blant KLAR, FERDIG, VENTER_SVAR, SUSPENDERT, eller FEILET")
    public List<ProsessTaskStatusDto> getProsessTaskStatuser() {
        return prosessTaskStatuser;
    }

    public void setProsessTaskStatuser(List<ProsessTaskStatusDto> prosessTaskStatuser) {
        this.prosessTaskStatuser = prosessTaskStatuser;
    }

    @ApiModelProperty(value = "Søker etter prosesstask med siste kjøring fra og med dette tidspunktet")
    public LocalDateTime getSisteKjoeretidspunktFraOgMed() {
        return sisteKjoeretidspunktFraOgMed;
    }

    public void setSisteKjoeretidspunktFraOgMed(LocalDateTime sisteKjoeretidspunktFraOgMed) {
        this.sisteKjoeretidspunktFraOgMed = sisteKjoeretidspunktFraOgMed;
    }

    @ApiModelProperty(value = "Søker etter prosesstask med siste kjøring til og med dette tidspunktet")
    public LocalDateTime getSisteKjoeretidspunktTilOgMed() {
        return sisteKjoeretidspunktTilOgMed;
    }

    public void setSisteKjoeretidspunktTilOgMed(LocalDateTime sisteKjoeretidspunktTilOgMed) {
        this.sisteKjoeretidspunktTilOgMed = sisteKjoeretidspunktTilOgMed;
    }

    public Sporingsdata lagSporingsloggData() {
        Sporingsdata sporingsdata = Sporingsdata.opprett();
        sporingsdata.leggTilId(StandardSporingsloggId.PROSESS_TASK_STATUS, prosessTaskStatuser.stream().map(ProsessTaskStatusDto::getProsessTaskStatusName).collect(Collectors.joining(",")));
        sporingsdata.leggTilId(StandardSporingsloggId.PROSESS_TASK_KJORETIDSINTERVALL, String.format("%s-%s", sisteKjoeretidspunktFraOgMed, sisteKjoeretidspunktTilOgMed));
        return sporingsdata;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett(); //denne er tom, ProsessTask-API har i praksis rollebasert tilgangskontroll
    }
}

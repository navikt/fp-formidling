package no.nav.vedtak.felles.prosesstask.rest.dto;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

public class ProsessTaskDataKonverter {

    private ProsessTaskDataKonverter() {
    }

    public static ProsessTaskDataDto tilProsessTaskDataDto(ProsessTaskData data) {
        ProsessTaskDataDto dto = new ProsessTaskDataDto();

        dto.setId(data.getId());
        dto.setTaskType(data.getTaskType());
        dto.setNesteKjøringEtter(data.getNesteKjøringEtter());
        dto.setGruppe(data.getGruppe());
        dto.setSekvens(data.getSekvens());
        dto.setStatus(data.getStatus().getDbKode());
        dto.setSistKjørt(data.getSistKjørt());
        dto.setSisteFeilKode(data.getSisteFeilKode());
        dto.setTaskParametre(data.getProperties());

        return dto;
    }

    public static FeiletProsessTaskDataDto tilFeiletProsessTaskDataDto(ProsessTaskData data) {
        FeiletProsessTaskDataDto dto = new FeiletProsessTaskDataDto();

        dto.setProsessTaskDataDto(tilProsessTaskDataDto(data));

        dto.setFeiledeForsøk(data.getAntallFeiledeForsøk());
        dto.setSisteFeilTekst(data.getSisteFeil());
        dto.setSisteKjøringServerProsess(data.getSisteKjøringServerProsess());

        return dto;
    }

    public static ProsessTaskDataPayloadDto tilProsessTaskDataPayloadDto(ProsessTaskData data) {
        ProsessTaskDataPayloadDto dto = new ProsessTaskDataPayloadDto();

        dto.setProsessTaskDataDto(tilProsessTaskDataDto(data));

        dto.setFeiledeForsøk(data.getAntallFeiledeForsøk());
        dto.setSisteFeilTekst(data.getSisteFeil());
        dto.setSisteKjøringServerProsess(data.getSisteKjøringServerProsess());

        dto.setPayload(data.getPayloadAsString());

        return dto;
    }

}

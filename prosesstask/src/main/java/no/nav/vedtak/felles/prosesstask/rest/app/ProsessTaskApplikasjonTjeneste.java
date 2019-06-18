package no.nav.vedtak.felles.prosesstask.rest.app;

import java.util.List;
import java.util.Optional;

import no.nav.vedtak.felles.prosesstask.rest.dto.FeiletProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataPayloadDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRestartInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRestartResultatDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRetryAllResultatDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.SokeFilterDto;

public interface ProsessTaskApplikasjonTjeneste {

    List<ProsessTaskDataDto> finnAlle(SokeFilterDto sokeFilterDto);

    Optional<FeiletProsessTaskDataDto> finnFeiletProsessTask(Long prosessTaskId);

    Optional<ProsessTaskDataPayloadDto> finnProsessTaskMedPayload(Long prosessTaskId);

    /**
     * Asynkron-restart av angitt prosesstask. Ny status plukkes opp av {@link no.nav.foreldrepenger.felles.prosesstask.impl.TaskManager}
     * som trigger den.
     */
    ProsessTaskRestartResultatDto flaggProsessTaskForRestart(ProsessTaskRestartInputDto prosessTaskRestartInputDto);

    ProsessTaskRetryAllResultatDto flaggAlleFeileteProsessTasksForRestart();

    List<ProsessTaskDataDto> finnStatusPåBatchTasks();
}

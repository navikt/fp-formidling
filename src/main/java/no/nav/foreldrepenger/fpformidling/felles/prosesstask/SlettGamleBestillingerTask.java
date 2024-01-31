package no.nav.foreldrepenger.fpformidling.felles.prosesstask;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.HendelseRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Dependent
@ProsessTask(value = "vedlikehold.tasks.slettgamle", cronExpression = "0 45 1 * * *", maxFailedRuns = 1)
public class SlettGamleBestillingerTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettGamleBestillingerTask.class);
    private final HendelseRepository hendelseRepository;

    @Inject
    public SlettGamleBestillingerTask(HendelseRepository hendelseRepository) {
        this.hendelseRepository = hendelseRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var bestillingerSlettet = hendelseRepository.slettDokumentHendelserEldreEnn(LocalDate.now().minusMonths(1));
        LOG.info("Slettet {} dokumentbestillinger som er over 1 måned gamle.", bestillingerSlettet);
    }

}

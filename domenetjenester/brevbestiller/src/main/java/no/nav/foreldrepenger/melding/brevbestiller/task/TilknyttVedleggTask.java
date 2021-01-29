package no.nav.foreldrepenger.melding.brevbestiller.task;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.integrasjon.journal.TilknyttVedleggTjeneste;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.nav.foreldrepenger.melding.brevbestiller.task.TilknyttVedleggTask.TASKTYPE;

@ApplicationScoped
@ProsessTask(TASKTYPE)
public class TilknyttVedleggTask implements ProsessTaskHandler {
    public static final String TASKTYPE = "formidling.tilknyttVedlegg";
    private TilknyttVedleggTjeneste tilknyttVedleggTjeneste;
    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public TilknyttVedleggTask(TilknyttVedleggTjeneste tilknyttVedleggTjeneste, DomeneobjektProvider domeneobjektProvider) {
        this.tilknyttVedleggTjeneste = tilknyttVedleggTjeneste;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        UUID behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BrevTaskProperties.BEHANDLING_UUID));
        JournalpostId journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        Behandling behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        Collection<InnsynDokument> vedlegg = filtrerUtDuplikater(domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter());

        tilknyttVedleggTjeneste.knyttAlleVedleggTilDokument(vedlegg, journalpostId);
    }

    private Collection<InnsynDokument> filtrerUtDuplikater(List<InnsynDokument> dokumenter) {
        return dokumenter.stream()
                .collect(Collectors.toConcurrentMap(InnsynDokument::getDokumentId, Function.identity(), (p, q) -> p))
                .values();
    }
}

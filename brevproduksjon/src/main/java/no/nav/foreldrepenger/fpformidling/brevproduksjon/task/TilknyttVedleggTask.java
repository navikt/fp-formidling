package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.TilknyttVedleggTjeneste;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.tilknyttVedlegg", maxFailedRuns = 2)
public class TilknyttVedleggTask implements ProsessTaskHandler {

    private final TilknyttVedleggTjeneste tilknyttVedleggTjeneste;
    private final DomeneobjektProvider domeneobjektProvider;

    @Inject
    public TilknyttVedleggTask(TilknyttVedleggTjeneste tilknyttVedleggTjeneste, DomeneobjektProvider domeneobjektProvider) {
        this.tilknyttVedleggTjeneste = tilknyttVedleggTjeneste;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = prosessTaskData.getBehandlingUuid();
        var journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        var behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        var vedlegg = filtrerUtDuplikater(domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter());

        tilknyttVedleggTjeneste.knyttAlleVedleggTilDokument(vedlegg, journalpostId);
    }

    private Collection<InnsynDokument> filtrerUtDuplikater(List<InnsynDokument> dokumenter) {
        return dokumenter.stream().collect(Collectors.toConcurrentMap(InnsynDokument::dokumentId, Function.identity(), (p, q) -> p)).values();
    }
}

package no.nav.foreldrepenger.fpformidling.brevproduksjon.task;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.InnsynBehandling;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.FpsakRestKlient;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.TilknyttVedleggTjeneste;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "formidling.tilknyttVedlegg", maxFailedRuns = 2)
public class TilknyttVedleggTask implements ProsessTaskHandler {

    private final TilknyttVedleggTjeneste tilknyttVedleggTjeneste;
    private final FpsakRestKlient fpsakRestKlient;

    @Inject
    public TilknyttVedleggTask(TilknyttVedleggTjeneste tilknyttVedleggTjeneste, FpsakRestKlient fpsakRestKlient) {
        this.tilknyttVedleggTjeneste = tilknyttVedleggTjeneste;
        this.fpsakRestKlient = fpsakRestKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingUuid = prosessTaskData.getBehandlingUuid();
        var journalpostId = new JournalpostId(prosessTaskData.getPropertyValue(BrevTaskProperties.JOURNALPOST_ID));
        var behandling = fpsakRestKlient.hentBrevGrunnlag(behandlingUuid);
        var innsyn = behandling.innsynBehandling();
        var vedlegg = filtrerUtDuplikater(innsyn.dokumenter());

        tilknyttVedleggTjeneste.knyttAlleVedleggTilDokument(vedlegg, journalpostId);
    }

    private Collection<InnsynBehandling.InnsynDokument> filtrerUtDuplikater(List<InnsynBehandling.InnsynDokument> dokumenter) {
        return dokumenter.stream()
            .collect(Collectors.toConcurrentMap(InnsynBehandling.InnsynDokument::dokumentId, Function.identity(), (p, q) -> p))
            .values();
    }
}

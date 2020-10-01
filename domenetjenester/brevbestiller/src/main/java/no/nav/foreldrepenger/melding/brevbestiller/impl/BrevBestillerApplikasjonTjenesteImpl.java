package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentbestillingDtoMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.util.env.Cluster;
import no.nav.vedtak.util.env.Environment;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {

    private static final Set<String> DOKGEN_MALER = Set.of(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD.getKode());
    private static final Environment ENV = Environment.current();

    private DokumentMalUtleder dokumentMalUtleder;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentbestillingDtoMapper dokumentbestillingDtoMapper;
    private DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerApplikasjonTjenesteImpl(DokumentMalUtleder dokumentMalUtleder,
                                                DomeneobjektProvider domeneobjektProvider,
                                                DokumentbestillingDtoMapper dokumentbestillingDtoMapper,
                                                DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste,
                                                DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste) {
        this.dokumentMalUtleder = dokumentMalUtleder;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentbestillingDtoMapper = dokumentbestillingDtoMapper;
        this.dokprodBrevproduksjonTjeneste = dokprodBrevproduksjonTjeneste;
        this.dokgenBrevproduksjonTjeneste = dokgenBrevproduksjonTjeneste;
    }

    @Override
    public byte[] forhandsvisBrev(DokumentbestillingDto dokumentbestillingDto) {
        DokumentHendelse dokumentHendelse = dokumentbestillingDtoMapper.mapDokumentbestillingFraDtoForEndepunkt(dokumentbestillingDto);
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);

        if (DOKGEN_MALER.contains(dokumentMal.getKode()) && !Cluster.PROD_FSS.equals(ENV.getCluster())) {
            return dokgenBrevproduksjonTjeneste.forhandsvisBrev(dokumentHendelse, behandling, dokumentMal);
        } else {
            return dokprodBrevproduksjonTjeneste.forhandsvisBrev(dokumentHendelse, behandling, dokumentMal);
        }
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);

        if (DOKGEN_MALER.contains(dokumentMal.getKode()) && !Cluster.PROD_FSS.equals(ENV.getCluster())) {
            return dokgenBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
        } else {
            return dokprodBrevproduksjonTjeneste.bestillBrev(dokumentHendelse, behandling, dokumentMal);
        }
    }
}

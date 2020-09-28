package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.*;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OppretteJournalpost {
    private static final Logger LOG = LoggerFactory.getLogger(OppretteJournalpost.class);
    private JournalpostRestKlient journalpostRestKlient;
    private static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";
    private static final String TEMA_FORELDREPENGER = "FOR";
    private static final String FAGSAK = "FAGSAK";

    OppretteJournalpost() {
        //CDI
    }

    @Inject
    public OppretteJournalpost(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public String journalFørUtsendelse(Dokument generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Long fagsakId) {
        LOG.info("Starter journalføring av brev sendt til {} med malkode {}", dokumentHendelse.getBehandlingUuid(), generertBrev.getBrevkode());

        OpprettetJournalpostResponse response = journalpostRestKlient.opprettJournalpost(lagJournalPostData(generertBrev, dokumentFelles, dokumentHendelse, fagsakId),true );

        if (!response.erFerdigstilt()) {
            LOG.warn("Journalpostid {} ble ikke ferdigstilt", response.getJournalpostId());
        }

        return response.getJournalpostId();
    }

    public JournalPostData lagJournalPostData(Dokument generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Long fagsakId) {
        AvsenderMottaker avsenderMottaker = new AvsenderMottaker(dokumentFelles.getMottakerId(), dokumentFelles.getMottakerNavn(), AvsenderMottakerIdType.FØDSELSNUMMER);
        Bruker bruker = new Bruker(dokumentFelles.getSakspartId(),BrukerIdType.FNR);
        Sak sak = new Sak(fagsakId.toString(), Fagsystem.FPSAK.getOffisiellKode(), FAGSAK);

        return new JournalPostData(
                avsenderMottaker,
                bruker,
                TEMA_FORELDREPENGER,
                mapBehandlingsTema(dokumentHendelse.getYtelseType()),
                dokumentHendelse.getTittel(),
                AUTOMATISK_JOURNALFØRENDE_ENHET,
                sak,
                List.of(generertBrev));
    }

    private String mapBehandlingsTema(FagsakYtelseType ytelseType) {
        return  ytelseType.gjelderEngangsstønad() ? BehandlingTema.ENGANGSSTØNAD.getOffisiellKode() :
                ytelseType.gjelderForeldrepenger() ? BehandlingTema.FORELDREPENGER.getOffisiellKode():
                ytelseType.gjelderSvangerskapspenger() ? BehandlingTema.SVANGERSKAPSPENGER.getOffisiellKode() : BehandlingTema.UDEFINERT.getOffisiellKode();
    }
}

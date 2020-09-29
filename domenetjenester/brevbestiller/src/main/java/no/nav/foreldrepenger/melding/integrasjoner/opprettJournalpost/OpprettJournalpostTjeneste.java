package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.AvsenderMottaker;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.AvsenderMottakerIdType;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.Bruker;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.BrukerIdType;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.DokumentOpprettRequest;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.Sak;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;

@ApplicationScoped
public class OpprettJournalpostTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettJournalpostTjeneste.class);
    private JournalpostRestKlient journalpostRestKlient;
    private static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";
    private static final String TEMA_FORELDREPENGER = "FOR";
    private static final String FAGSAK = "FAGSAK";

    OpprettJournalpostTjeneste() {
        //CDI
    }

    @Inject
    public OpprettJournalpostTjeneste(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public OpprettJournalpostResponse journalførUtsendelse(DokumentOpprettRequest generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Long fagsakId, boolean ferdigstill) {
        LOG.info("Starter journalføring av brev sendt for behandling {} med malkode {}", dokumentHendelse.getBehandlingUuid(), generertBrev.getBrevkode());

        try {
            OpprettJournalpostResponse response = journalpostRestKlient.opprettJournalpost(lagRequest(generertBrev, dokumentFelles, dokumentHendelse, fagsakId), ferdigstill);

            if (ferdigstill && !response.erFerdigstilt()) {
                LOG.warn("Journalpost {} ble ikke ferdigstilt", response.getJournalpostId());
            }

            return response;
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteJournalpost(dokumentHendelse.getBehandlingUuid().toString(), generertBrev.getBrevkode(), e).toException();
        }
    }

    private OpprettJournalpostRequest lagRequest(DokumentOpprettRequest generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Long fagsakId) {
        AvsenderMottaker avsenderMottaker = new AvsenderMottaker(dokumentFelles.getMottakerId(), dokumentFelles.getMottakerNavn(), AvsenderMottakerIdType.FØDSELSNUMMER);
        Bruker bruker = new Bruker(dokumentFelles.getSakspartId(),BrukerIdType.FNR);
        Sak sak = new Sak(fagsakId.toString(), Fagsystem.FPSAK.getOffisiellKode(), FAGSAK);

        return new OpprettJournalpostRequest(
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
        return ytelseType.gjelderEngangsstønad() ? BehandlingTema.ENGANGSSTØNAD.getOffisiellKode() :
               ytelseType.gjelderForeldrepenger() ? BehandlingTema.FORELDREPENGER.getOffisiellKode():
               ytelseType.gjelderSvangerskapspenger() ? BehandlingTema.SVANGERSKAPSPENGER.getOffisiellKode() : BehandlingTema.UDEFINERT.getOffisiellKode();
    }
}

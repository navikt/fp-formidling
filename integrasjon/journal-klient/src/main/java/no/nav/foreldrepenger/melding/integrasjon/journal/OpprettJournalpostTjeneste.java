package no.nav.foreldrepenger.melding.integrasjon.journal;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.AvsenderMottaker;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.AvsenderMottakerIdType;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.Bruker;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.BrukerIdType;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.Sak;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

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

    public OpprettJournalpostResponse journalførUtsendelse(DokumentOpprettRequest generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Saksnummer saksnummer, boolean ferdigstill) {
        LOG.info("Starter journalføring av brev sendt for behandling {} med malkode {}", dokumentHendelse.getBehandlingUuid(), generertBrev.getBrevkode());

        try {
            OpprettJournalpostResponse response = journalpostRestKlient.opprettJournalpost(lagRequest(generertBrev, dokumentFelles, dokumentHendelse, saksnummer), ferdigstill);

            if (ferdigstill && !response.erFerdigstilt()) {
                LOG.warn("Journalpost {} ble ikke ferdigstilt", response.getJournalpostId());
            }

            return response;
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteJournalpost(dokumentHendelse.getBehandlingUuid().toString(), generertBrev.getBrevkode(), e).toException();
        }
    }

    private OpprettJournalpostRequest lagRequest(DokumentOpprettRequest generertBrev, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Saksnummer saksnummer) {
        AvsenderMottaker avsenderMottaker = new AvsenderMottaker(dokumentFelles.getMottakerId(), dokumentFelles.getMottakerNavn(), AvsenderMottakerIdType.FØDSELSNUMMER);
        Bruker bruker = new Bruker(dokumentFelles.getSakspartId(),BrukerIdType.FNR);
        Sak sak = new Sak(saksnummer.getVerdi(), Fagsystem.FPSAK.getOffisiellKode(), FAGSAK);

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

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
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

@ApplicationScoped
public class OpprettJournalpostTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettJournalpostTjeneste.class);
    private JournalpostRestKlient journalpostRestKlient;
    private static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";
    private static final String TEMA_FORELDREPENGER = "FOR";
    private static final String SAKSTYPE = "ARKIVSAK";
    private static final String ARKIVSAKSYSTEM = "GSAK";

    OpprettJournalpostTjeneste() {
        //CDI
    }

    @Inject
    public OpprettJournalpostTjeneste(JournalpostRestKlient journalpostRestKlient) {
        this.journalpostRestKlient = journalpostRestKlient;
    }

    public OpprettJournalpostResponse journalførUtsendelse(byte[] brev, DokumentMalType dokumentMalType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Saksnummer saksnummer, boolean ferdigstill) {
        LOG.info("Starter journalføring av brev for behandling {} med malkode {}", dokumentHendelse.getBehandlingUuid(), dokumentMalType.getKode());

        try {
            OpprettJournalpostResponse response = journalpostRestKlient.opprettJournalpost(lagRequest(brev, dokumentMalType, dokumentFelles, dokumentHendelse, saksnummer), ferdigstill);

            if (ferdigstill && !response.erFerdigstilt()) {
                LOG.warn("Journalpost {} ble ikke ferdigstilt", response.getJournalpostId());
            }

            LOG.info("Journalføring for behandling {} med malkode {} ferdig med response: {}", dokumentHendelse.getBehandlingUuid(), dokumentMalType.getKode(), response.toString()); //NOSONAR
            return response;
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteJournalpost(dokumentHendelse.getBehandlingUuid().toString(), dokumentMalType.getKode(), e).toException();
        }
    }

    private OpprettJournalpostRequest lagRequest(byte[] brev, DokumentMalType dokumentMalType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Saksnummer saksnummer) {
        DokumentOpprettRequest dokument = new DokumentOpprettRequest(getTittel(dokumentHendelse, dokumentMalType), dokumentMalType.getKode(), null, brev);

        AvsenderMottaker avsenderMottaker = new AvsenderMottaker(dokumentFelles.getMottakerId(), dokumentFelles.getMottakerNavn(), hentAvsenderMotakkerType(dokumentFelles.getMottakerType()));
        Bruker bruker = new Bruker(dokumentFelles.getSakspartId(), BrukerIdType.FNR);
        Sak sak = new Sak(SAKSTYPE, saksnummer.getVerdi(), ARKIVSAKSYSTEM);

        return new OpprettJournalpostRequest(
                avsenderMottaker,
                bruker,
                TEMA_FORELDREPENGER,
                mapBehandlingsTema(dokumentHendelse.getYtelseType()),
                getTittel(dokumentHendelse, dokumentMalType),
                AUTOMATISK_JOURNALFØRENDE_ENHET,
                sak,
                List.of(dokument));
    }

    private String getTittel(DokumentHendelse dokumentHendelse, DokumentMalType dokumentMalType) {
        return dokumentHendelse.getTittel() != null ? dokumentHendelse.getTittel() : dokumentMalType.getNavn();
    }

    private String mapBehandlingsTema(FagsakYtelseType ytelseType) {
        return ytelseType.gjelderEngangsstønad() ? BehandlingTema.ENGANGSSTØNAD.getOffisiellKode() :
               ytelseType.gjelderForeldrepenger() ? BehandlingTema.FORELDREPENGER.getOffisiellKode() :
               ytelseType.gjelderSvangerskapspenger() ? BehandlingTema.SVANGERSKAPSPENGER.getOffisiellKode() :
                       BehandlingTema.UDEFINERT.getOffisiellKode(); //NOSONAR
    }
    private AvsenderMottakerIdType hentAvsenderMotakkerType(DokumentFelles.MottakerType mottakerType) {
        return mottakerType.equals(DokumentFelles.MottakerType.PERSON) ? AvsenderMottakerIdType.FNR : AvsenderMottakerIdType.ORGNR;
    }
}

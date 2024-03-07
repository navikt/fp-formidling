package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.dokarkiv.DokArkiv;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.AvsenderMottaker;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.Bruker;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.DokumentInfoOpprett;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.Dokumentvariant;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostRequest;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostResponse;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.Sak;

@ApplicationScoped
public class OpprettJournalpostTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OpprettJournalpostTjeneste.class);

    private DokArkiv dokArkivKlient;

    OpprettJournalpostTjeneste() {
        // CDI
    }

    @Inject
    public OpprettJournalpostTjeneste(DokArkiv dokArkivKlient) {
        this.dokArkivKlient = dokArkivKlient;
    }

    public OpprettJournalpostResponse journalførUtsendelse(byte[] brev,
                                                           DokumentMalType dokumentMalType,
                                                           DokumentFelles dokumentFelles,
                                                           DokumentHendelse dokumentHendelse,
                                                           Saksnummer saksnummer,
                                                           boolean ferdigstill, String unikReferanse,
                                                           DokumentMalType journalførSomDokument,
                                                           FagsakYtelseType ytelseType) {
        LOG.info("Starter journalføring av brev for behandling {} med malkode {}", dokumentHendelse.getBehandlingUuid(), dokumentMalType.getKode());

        try {
            var requestBuilder = lagRequestBuilder(brev, dokumentFelles, saksnummer, ferdigstill, unikReferanse, journalførSomDokument, ytelseType);
            var response = dokArkivKlient.opprettJournalpost(requestBuilder.build(), ferdigstill);

            if (LOG.isWarnEnabled() && ferdigstill && !response.journalpostferdigstilt()) {
                LOG.warn("Journalpost {} ble ikke ferdigstilt", response.journalpostId());

            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Journalføring for behandling {} med malkode {} ferdig med response: {}", dokumentHendelse.getBehandlingUuid(),
                    dokumentMalType.getKode(), response);
            }

            return response;
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-156533",
                String.format("Journalføring av brev for behandling %s med mal %s feilet.", dokumentHendelse.getBehandlingUuid().toString(),
                    dokumentMalType.getKode()), e);
        }
    }

    private OpprettJournalpostRequest.OpprettJournalpostRequestBuilder lagRequestBuilder(byte[] brev,
                                                                                         DokumentFelles dokumentFelles,
                                                                                         Saksnummer saksnummer,
                                                                                         boolean ferdigstill,
                                                                                         String bestillingsUidMedUnikReferanse,
                                                                                         DokumentMalType journalførSomDokument,
                                                                                         FagsakYtelseType ytelseType) {
        var tittel = journalførSomDokument.getNavn();

        var dokument = DokumentInfoOpprett.builder()
            .medTittel(tittel)
            .medBrevkode(journalførSomDokument.getKode())
            .leggTilDokumentvariant(new Dokumentvariant(Dokumentvariant.Variantformat.ARKIV, Dokumentvariant.Filtype.PDFA, brev))
            .build();

        return OpprettJournalpostRequest.nyUtgående()
            .medTittel(tittel)
            .medSak(sak(saksnummer))
            .medTema(DokArkivKlient.TEMA_FORELDREPENGER)
            .medBehandlingstema(mapBehandlingsTema(ytelseType))
            .medJournalfoerendeEnhet(ferdigstill ? DokArkivKlient.AUTOMATISK_JOURNALFØRENDE_ENHET : null)
            .medEksternReferanseId(bestillingsUidMedUnikReferanse)
            .medBruker(bruker(dokumentFelles))
            .medAvsenderMottaker(mottaker(dokumentFelles))
            .medDokumenter(List.of(dokument));

    }

    private static Bruker bruker(DokumentFelles dokumentFelles) {
        return new Bruker(dokumentFelles.getSakspartId(), Bruker.BrukerIdType.FNR);
    }


    private Sak sak(Saksnummer saksnummer) {
        return new Sak(saksnummer.getVerdi(), Fagsystem.FPSAK.getOffisiellKode(), Sak.Sakstype.FAGSAK);
    }

    private String mapBehandlingsTema(FagsakYtelseType ytelseType) {
        return switch (ytelseType) {
            case ENGANGSTØNAD -> BehandlingTema.ENGANGSSTØNAD.getOffisiellKode();
            case FORELDREPENGER -> BehandlingTema.FORELDREPENGER.getOffisiellKode();
            case SVANGERSKAPSPENGER -> BehandlingTema.SVANGERSKAPSPENGER.getOffisiellKode();
            case UDEFINERT -> BehandlingTema.UDEFINERT.getOffisiellKode();
        };
    }

    private AvsenderMottaker mottaker(DokumentFelles dokumentFelles) {
        return new AvsenderMottaker(dokumentFelles.getMottakerId(), mapMottakerType(dokumentFelles.getMottakerType()),
            dokumentFelles.getMottakerNavn());
    }

    private AvsenderMottaker.AvsenderMottakerIdType mapMottakerType(DokumentFelles.MottakerType mottakerType) {
        return switch (mottakerType) {
            case PERSON -> AvsenderMottaker.AvsenderMottakerIdType.FNR;
            case ORGANISASJON -> AvsenderMottaker.AvsenderMottakerIdType.ORGNR;
        };
    }
}

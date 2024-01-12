package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.domene.typer.Saksnummer;
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
                                                           boolean ferdigstill,
                                                           String overskriftVedFritekstBrev,
                                                           String unikReferanse, DokumentMalType originalDokumentType) {
        LOG.info("Starter journalføring av brev for behandling {} med malkode {}", dokumentHendelse.getBehandlingUuid(), dokumentMalType.getKode());

        try {
            var requestBuilder = lagRequestBuilder(brev, dokumentMalType, dokumentFelles, dokumentHendelse, saksnummer, ferdigstill,
                overskriftVedFritekstBrev, unikReferanse, originalDokumentType);
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
                                                                                         DokumentMalType dokumentMal,
                                                                                         DokumentFelles dokumentFelles,
                                                                                         DokumentHendelse dokumentHendelse,
                                                                                         Saksnummer saksnummer,
                                                                                         boolean ferdigstill,
                                                                                         String overskriftVedFritekstbrev,
                                                                                         String bestillingsUidMedUnikReferanse,
                                                                                         DokumentMalType originalDokumentType) {
        var tittel = getTittel(dokumentHendelse, dokumentMal, overskriftVedFritekstbrev, originalDokumentType);
        var dokument = DokumentInfoOpprett.builder()
            .medTittel(tittel)
            .medBrevkode(originalDokumentType.getKode())
            .leggTilDokumentvariant(new Dokumentvariant(Dokumentvariant.Variantformat.ARKIV, Dokumentvariant.Filtype.PDFA, brev))
            .build();
        var bruker = new Bruker(dokumentFelles.getSakspartId(), Bruker.BrukerIdType.FNR);
        var avsenderMottaker = new AvsenderMottaker(dokumentFelles.getMottakerId(), hentAvsenderMottakerType(dokumentFelles.getMottakerType()),
            dokumentFelles.getMottakerNavn());
        return OpprettJournalpostRequest.nyUtgående()
            .medTittel(tittel)
            .medSak(sak(saksnummer))
            .medTema(DokArkivKlient.TEMA_FORELDREPENGER)
            .medBehandlingstema(mapBehandlingsTema(dokumentHendelse.getYtelseType()))
            .medJournalfoerendeEnhet(ferdigstill ? DokArkivKlient.AUTOMATISK_JOURNALFØRENDE_ENHET : null)
            .medEksternReferanseId(bestillingsUidMedUnikReferanse)
            .medBruker(bruker)
            .medAvsenderMottaker(avsenderMottaker)
            .medDokumenter(List.of(dokument));

    }

    private Sak sak(Saksnummer saksnummer) {
        return new Sak(saksnummer.getVerdi(), Fagsystem.FPSAK.getOffisiellKode(), Sak.Sakstype.FAGSAK);
    }

    private String getTittel(DokumentHendelse dokumentHendelse,
                             DokumentMalType dokumentMal,
                             String overskriftVedFritekstbrev,
                             DokumentMalType originalDokumentType) {
        if (dokumentHendelse.getTittel() != null) {
            return dokumentHendelse.getTittel();
        } else if (DokumentMalType.FRITEKSTBREV.equals(dokumentMal) && overskriftVedFritekstbrev != null) {
            return overskriftVedFritekstbrev;
        } else {
            return originalDokumentType.getNavn();
        }
    }

    private String mapBehandlingsTema(FagsakYtelseType ytelseType) {
        return switch (ytelseType) {
            case ENGANGSTØNAD -> BehandlingTema.ENGANGSSTØNAD.getOffisiellKode();
            case FORELDREPENGER -> BehandlingTema.FORELDREPENGER.getOffisiellKode();
            case SVANGERSKAPSPENGER -> BehandlingTema.SVANGERSKAPSPENGER.getOffisiellKode();
            case UDEFINERT -> BehandlingTema.UDEFINERT.getOffisiellKode();
        };
    }

    private AvsenderMottaker.AvsenderMottakerIdType hentAvsenderMottakerType(DokumentFelles.MottakerType mottakerType) {
        return mottakerType.equals(
            DokumentFelles.MottakerType.PERSON) ? AvsenderMottaker.AvsenderMottakerIdType.FNR : AvsenderMottaker.AvsenderMottakerIdType.ORGNR;
    }
}

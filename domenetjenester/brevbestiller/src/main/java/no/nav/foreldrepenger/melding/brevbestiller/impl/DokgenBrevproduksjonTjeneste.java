package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.brevbestiller.BrevbestillerFeil;
import no.nav.foreldrepenger.melding.brevbestiller.JsonMapper;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.BestillingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.integrasjon.dokdist.DokdistRestKlient;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.DokgenRestKlient;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.journal.FerdigstillJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.TilknyttVedleggTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste implements BrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;
    private DokgenRestKlient dokgenRestKlient;
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    private TilknyttVedleggTjeneste tilknyttVedleggTjeneste;
    private FerdigstillJournalpostTjeneste ferdigstillJournalpostTjeneste;
    private DokdistRestKlient dokdistRestKlient;
    private DokumentdataMapperProvider dokumentdataMapperProvider;

    DokgenBrevproduksjonTjeneste() {
        // CDI
    }

    @Inject
    public DokgenBrevproduksjonTjeneste(DokumentFellesDataMapper dokumentFellesDataMapper,
                                        DomeneobjektProvider domeneobjektProvider,
                                        DokumentRepository dokumentRepository,
                                        DokgenRestKlient dokgenRestKlient,
                                        OpprettJournalpostTjeneste opprettJournalpostTjeneste,
                                        TilknyttVedleggTjeneste tilknyttVedleggTjeneste,
                                        FerdigstillJournalpostTjeneste ferdigstillJournalpostTjeneste,
                                        DokdistRestKlient dokdistRestKlient,
                                        DokumentdataMapperProvider dokumentdataMapperProvider) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
        this.dokgenRestKlient = dokgenRestKlient;
        this.opprettJournalpostTjeneste = opprettJournalpostTjeneste;
        this.tilknyttVedleggTjeneste = tilknyttVedleggTjeneste;
        this.ferdigstillJournalpostTjeneste = ferdigstillJournalpostTjeneste;
        this.dokdistRestKlient = dokdistRestKlient;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(førsteDokumentFelles, dokumentHendelse, behandling);
        byte[] brev;
        try {
            brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            throw BrevbestillerFeil.FACTORY.klarteIkkeForhåndvise(dokumentMal.getKode(), behandling.getUuid().toString(), e).toException();
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return brev;
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.BESTILL);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        Collection<InnsynDokument> vedlegg = finnEventuelleVedlegg(behandling, dokumentMal);
        boolean harVedlegg = !vedlegg.isEmpty();
        List<DokumentHistorikkinnslag> historikkinnslag = new ArrayList<>();

        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
            Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);
            dokumentFelles.setBrevData(JsonMapper.toJson(dokumentdata));

            byte[] brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
            OpprettJournalpostResponse response = opprettJournalpostTjeneste.journalførUtsendelse(brev, dokumentMal, dokumentFelles, dokumentHendelse, behandling.getFagsakBackend().getSaksnummer(), !harVedlegg);
            JournalpostId journalpostId = new JournalpostId(response.getJournalpostId());
            dokdistRestKlient.distribuerJournalpost(journalpostId);

            if (harVedlegg) {
                tilknyttVedleggTjeneste.knyttAlleVedleggTilDokument(vedlegg, journalpostId);
                ferdigstillJournalpostTjeneste.ferdigstillForsendelse(journalpostId);
                //TODO kanseller forsendelse hvis det feiler
            }

            DokumentHistorikkinnslag nyttHistorikkinnslag = lagHistorikkinnslag(dokumentHendelse, response, dokumentMal);
            LOGGER.info("Opprettet historikkinnslag for bestilt brev: {}", nyttHistorikkinnslag.toString());
            historikkinnslag.add(nyttHistorikkinnslag);
        }
        return historikkinnslag;
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }

    private Collection<InnsynDokument> finnEventuelleVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYNSKRAV_SVAR.equals(dokumentMal)) {
            return Collections.emptyList();
        }
        return filtrerUtDuplikater(domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter());
    }

    public static Collection<InnsynDokument> filtrerUtDuplikater(List<InnsynDokument> dokumenter) {
        return dokumenter
                .stream()
                .collect(Collectors.toConcurrentMap(InnsynDokument::getDokumentId, Function.identity(), (p, q) -> p))
                .values();
    }

    private DokumentHistorikkinnslag lagHistorikkinnslag(DokumentHendelse dokumentHendelse,
                                                         OpprettJournalpostResponse response,
                                                         DokumentMalType dokumentMal) {
        return DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(dokumentHendelse.getBehandlingUuid())
                .medHistorikkUuid(UUID.randomUUID())
                .medHendelseId(dokumentHendelse.getId())
                .medJournalpostId(new JournalpostId(response.getJournalpostId()))
                .medDokumentId(response.getDokumenter().get(0).getDokumentInfoId())
                .medHistorikkAktør(dokumentHendelse.getHistorikkAktør() != null ?
                        dokumentHendelse.getHistorikkAktør() : HistorikkAktør.VEDTAKSLØSNINGEN)
                .medDokumentMalType(dokumentMal)
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
    }
}

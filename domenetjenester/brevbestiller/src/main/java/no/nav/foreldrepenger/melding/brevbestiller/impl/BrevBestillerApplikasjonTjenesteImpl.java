package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.XmlUtil.elementTilString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.brevbestiller.BrevbestillerFeil;
import no.nav.foreldrepenger.melding.brevbestiller.DokumentbestillingMapper;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dtomapper.DokumentHendelseDtoMapper;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Dokumentbestillingsinformasjon;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentResponse;
import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonConsumer;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrevBestillerApplikasjonTjenesteImpl.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokumentproduksjonConsumer dokumentproduksjonProxyService;
    private DokumentXmlDataMapper dokumentXmlDataMapper;
    private DokumentMalUtleder dokumentMalUtleder;
    private DokumentbestillingMapper dokumentbestillingMapper;
    private DokumentHendelseDtoMapper dtoTilDomeneobjektMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerApplikasjonTjenesteImpl(DokumentproduksjonConsumer dokumentproduksjonProxyService,
                                                DokumentXmlDataMapper dokumentXmlDataMapper,
                                                DokumentMalUtleder dokumentMalUtleder,
                                                DokumentbestillingMapper dokumentbestillingMapper,
                                                DokumentHendelseDtoMapper dtoTilDomeneobjektMapper,
                                                DokumentFellesDataMapper dokumentFellesDataMapper,
                                                DomeneobjektProvider domeneobjektProvider,
                                                DokumentRepository dokumentRepository) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
        this.dokumentXmlDataMapper = dokumentXmlDataMapper;
        this.dokumentMalUtleder = dokumentMalUtleder;
        this.dokumentbestillingMapper = dokumentbestillingMapper;
        this.dtoTilDomeneobjektMapper = dtoTilDomeneobjektMapper;
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
    }

    @Override
    public DokumentHistorikkinnslag bestillBrev(DokumentHendelse dokumentHendelse) {
        Behandling behandling = domeneobjektProvider.hentBehandling(dokumentHendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, dokumentHendelse);
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, "Bestill");
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData);
        //FIXME : RS - den tar ikke hensyn til at det kan være flere mottakere
        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentData.getFørsteDokumentFelles(), dokumentHendelse, behandling);
        final DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();
        List<InnsynDokument> vedlegg = finnEventuelleVedlegg(behandling, dokumentMal);

        boolean harVedlegg = !vedlegg.isEmpty();
        final Dokumentbestillingsinformasjon dokumentbestillingsinformasjon = dokumentbestillingMapper.mapFraBehandling(dokumentMal,
                førsteDokumentFelles, harVedlegg);

        førsteDokumentFelles.setXml(elementTilString(brevXmlElement));
        dokumentRepository.lagre(dokumentData);

        ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokumentResponse = produserIkkeredigerbartDokument(brevXmlElement, dokumentbestillingsinformasjon);
        if (harVedlegg) {
            String journalpostId = produserIkkeredigerbartDokumentResponse.getJournalpostId();
            knyttAlleVedleggTilDokument(vedlegg, journalpostId, behandling.getEndretAv());
            ferdigstillForsendelse(journalpostId, behandling.getEndretAv());
        }
        return lagHistorikkinnslag(dokumentHendelse, produserIkkeredigerbartDokumentResponse, dokumentMal);
    }

    private void ferdigstillForsendelse(String journalpostId, String endretAvNavn) {
        FerdigstillForsendelseRequest request = new FerdigstillForsendelseRequest();
        request.setJournalpostId(journalpostId);
        request.setEndretAvNavn(endretAvNavn);
        try {
            dokumentproduksjonProxyService.ferdigstillForsendelse(request);
        } catch (Exception e) {
            throw DokumentMapperFeil.FACTORY.ferdigstillingAvDokumentFeil(journalpostId, e).toException();
        }
    }

    private void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, String journalpostId, String endretAv) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.getJournalpostId(), v.getDokumentId(), endretAv));
    }

    private void knyttVedleggTilForsendelse(String knyttesTilJournalpostId, String knyttesFraJournalpostId, String dokumentId,
                                            String endretAvNavn) {
        KnyttVedleggTilForsendelseRequest request = new KnyttVedleggTilForsendelseRequest();
        request.setDokumentId(dokumentId);
        request.setEndretAvNavn(endretAvNavn);
        request.setKnyttesFraJournalpostId(knyttesFraJournalpostId);
        request.setKnyttesTilJournalpostId(knyttesTilJournalpostId);
        try {
            dokumentproduksjonProxyService.knyttVedleggTilForsendelse(request);
        } catch (Exception e) {
            throw DokumentMapperFeil.FACTORY.knyttingAvVedleggFeil(dokumentId, e).toException();
        }
    }

    private List<InnsynDokument> finnEventuelleVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYNSKRAV_SVAR.equals(dokumentMal.getKode())) {
            return Collections.emptyList();
        }
        return domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter();
    }

    private ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokument(Element
                                                                                            brevXmlElement, Dokumentbestillingsinformasjon dokumentbestillingsinformasjon) {
        ProduserIkkeredigerbartDokumentRequest produserIkkeredigerbartDokumentRequest = new ProduserIkkeredigerbartDokumentRequest();
        produserIkkeredigerbartDokumentRequest.setBrevdata(brevXmlElement);
        produserIkkeredigerbartDokumentRequest.setDokumentbestillingsinformasjon(dokumentbestillingsinformasjon);
        try {
            return dokumentproduksjonProxyService
                    .produserIkkeredigerbartDokument(produserIkkeredigerbartDokumentRequest);
        } catch (ProduserIkkeredigerbartDokumentDokumentErRedigerbart | ProduserIkkeredigerbartDokumentDokumentErVedlegg funksjonellFeil) {
            throw BrevbestillerFeil.FACTORY.feilFraDokumentProduksjon(funksjonellFeil).toException();
        }
    }


    private DokumentHistorikkinnslag lagHistorikkinnslag(DokumentHendelse dokumentHendelse,
                                                         ProduserIkkeredigerbartDokumentResponse response,
                                                         DokumentMalType dokumentMal) {
        return DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(dokumentHendelse.getBehandlingUuid())
                .medHendelseId(dokumentHendelse.getId())
                .medJournalpostId(new JournalpostId(response.getJournalpostId()))
                .medDokumentId(response.getDokumentId())
                .medHistorikkAktør(dokumentHendelse.getHistorikkAktør() != null ?
                        dokumentHendelse.getHistorikkAktør() : HistorikkAktør.VEDTAKSLØSNINGEN)
                .medDokumentMalType(dokumentMal)
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelseDto hendelseDto) {
        byte[] dokument;
        DokumentHendelse hendelse = dtoTilDomeneobjektMapper.mapDokumentHendelseFraDto(hendelseDto);
        Behandling behandling = domeneobjektProvider.hentBehandling(hendelse.getBehandlingUuid());
        DokumentMalType dokumentMal = dokumentMalUtleder.utledDokumentmal(behandling, hendelse);

        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, "Forhåndsvis");
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData);
        final DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentMal, førsteDokumentFelles, hendelse, behandling);

        førsteDokumentFelles.setXml(elementTilString(brevXmlElement));
        dokumentRepository.lagre(dokumentData);

        dokument = forhåndsvis(dokumentMal, brevXmlElement);
        if (dokument == null) {
            LOGGER.error("Klarte ikke hente behandling: {}", behandling.getId());
            throw BrevbestillerFeil.FACTORY.klarteIkkeForhåndvise(dokumentMal.getKode(), behandling.getUuid().toString()).toException();
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return dokument;
    }

    private byte[] forhåndsvis(DokumentMalType dokumentMal, Element brevXmlElement) {
        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
        produserDokumentutkastRequest.setDokumenttypeId(dokumentMal.getDoksysKode());
        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService.produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            return produserDokumentutkastResponse.getDokumentutkast();//$NON-NLS-1$
        }
        return null;
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, String bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType)
                .build();
    }
}

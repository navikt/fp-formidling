package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.XmlUtil.elementTilString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.brevbestiller.BrevbestillerFeil;
import no.nav.foreldrepenger.melding.brevbestiller.DokumentbestillingMapper;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.brev.FritekstmalBrevMapper;
import no.nav.foreldrepenger.melding.dokumentdata.BestillingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentproduksjon.v2.DokumentproduksjonConsumer;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
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

@ApplicationScoped
public class DokprodBrevproduksjonTjeneste implements BrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokprodBrevproduksjonTjeneste.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokumentproduksjonConsumer dokumentproduksjonProxyService;
    private DokumentbestillingMapper dokumentbestillingMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;

    public DokprodBrevproduksjonTjeneste() {
        // for cdi proxy
    }

    @Inject
    public DokprodBrevproduksjonTjeneste(DokumentproduksjonConsumer dokumentproduksjonProxyService,
                                         DokumentbestillingMapper dokumentbestillingMapper,
                                         DokumentFellesDataMapper dokumentFellesDataMapper,
                                         DomeneobjektProvider domeneobjektProvider,
                                         DokumentRepository dokumentRepository) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
        this.dokumentbestillingMapper = dokumentbestillingMapper;
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.BESTILL);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        Collection<InnsynDokument> vedlegg = finnEventuelleVedlegg(behandling, dokumentMal);
        boolean harVedlegg = !vedlegg.isEmpty();

        List<DokumentHistorikkinnslag> historikkinnslagList = new ArrayList<>();
        dokumentRepository.lagre(dokumentData);

        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            Element brevXmlElement = DokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentFelles, dokumentHendelse, behandling);
            dokumentFelles.setBrevData(elementTilString(brevXmlElement));

            final Dokumentbestillingsinformasjon dokumentbestillingsinformasjon = dokumentbestillingMapper.mapFraBehandling(dokumentMal,
                    dokumentFelles, harVedlegg);
            hentJournalpostTittel(DokumentXmlDataMapper.velgDokumentMapper(dokumentMal)).ifPresent(dokumentbestillingsinformasjon::setUstrukturertTittel);

            ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokumentResponse = produserIkkeredigerbartDokument(brevXmlElement, dokumentbestillingsinformasjon);
            if (harVedlegg) {
                JournalpostId journalpostId = new JournalpostId(produserIkkeredigerbartDokumentResponse.getJournalpostId());
                knyttAlleVedleggTilDokument(vedlegg, journalpostId, behandling.getEndretAv());
                ferdigstillForsendelse(journalpostId, behandling.getEndretAv());
                //TODO kanseller forsendelse hvis det feiler
            }

            historikkinnslagList.add(lagHistorikkinnslag(dokumentHendelse, produserIkkeredigerbartDokumentResponse, dokumentMal));
        }

        return historikkinnslagList;
    }

    private Optional<String> hentJournalpostTittel(DokumentTypeMapper dokumentTypeMapper) {
        if (dokumentTypeMapper instanceof FritekstmalBrevMapper) {
            return Optional.of(((FritekstmalBrevMapper) dokumentTypeMapper).displayName());
        }
        return Optional.empty();
    }

    private void ferdigstillForsendelse(JournalpostId journalpostId, String endretAvNavn) {
        FerdigstillForsendelseRequest request = new FerdigstillForsendelseRequest();
        request.setJournalpostId(journalpostId.getVerdi());
        request.setEndretAvNavn(endretAvNavn);
        try {
            dokumentproduksjonProxyService.ferdigstillForsendelse(request);
        } catch (Exception e) {
            throw DokumentMapperFeil.FACTORY.ferdigstillingAvDokumentFeil(journalpostId, e).toException();
        }
    }

    private void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, JournalpostId journalpostId, String endretAv) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.getJournalpostId(), v.getDokumentId(), endretAv));
    }

    private void knyttVedleggTilForsendelse(JournalpostId knyttesTilJournalpostId, JournalpostId knyttesFraJournalpostId, String dokumentId,
                                            String endretAvNavn) {
        KnyttVedleggTilForsendelseRequest request = new KnyttVedleggTilForsendelseRequest();
        request.setDokumentId(dokumentId);
        request.setEndretAvNavn(endretAvNavn);
        request.setKnyttesFraJournalpostId(knyttesFraJournalpostId.getVerdi());
        request.setKnyttesTilJournalpostId(knyttesTilJournalpostId.getVerdi());
        try {
            dokumentproduksjonProxyService.knyttVedleggTilForsendelse(request);
        } catch (Exception e) {
            throw DokumentMapperFeil.FACTORY.knyttingAvVedleggFeil(dokumentId, e).toException();
        }
    }

    private Collection<InnsynDokument> finnEventuelleVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYNSKRAV_SVAR.equals(dokumentMal.getKode())) {
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

    private ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokument(Element brevXmlElement,
                                                                                    Dokumentbestillingsinformasjon dokumentbestillingsinformasjon) {
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
                .medHistorikkUuid(UUID.randomUUID())
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
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        byte[] dokument;
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);

        final DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        Element brevXmlElement = DokumentXmlDataMapper.mapTilBrevXml(dokumentMal, førsteDokumentFelles, dokumentHendelse, behandling);

        førsteDokumentFelles.setBrevData(elementTilString(brevXmlElement));
        dokumentRepository.lagre(dokumentData);

        dokument = forhåndsvis(dokumentMal, brevXmlElement);
        if (dokument == null) {
            LOGGER.error("Klarte ikke hente behandling: {}", behandling.getId());
            throw BrevbestillerFeil.FACTORY.klarteIkkeForhåndviseDokprodbrev(dokumentMal.getKode(), behandling.getUuid().toString()).toException();
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return dokument;
    }

    private byte[] forhåndsvis(DokumentMalType dokumentMal, Element brevXmlElement) {
        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
        produserDokumentutkastRequest.setDokumenttypeId(dokumentMal.getDokSysKode().getKode());
        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService.produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            return produserDokumentutkastResponse.getDokumentutkast();//$NON-NLS-1$
        }
        return null;
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }
}

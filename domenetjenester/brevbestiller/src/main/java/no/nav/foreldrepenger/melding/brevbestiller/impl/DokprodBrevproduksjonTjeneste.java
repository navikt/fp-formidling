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

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.brevbestiller.DokumentbestillingMapper;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.brev.FritekstmalBrevMapper;
import no.nav.foreldrepenger.melding.dokumentdata.BestillingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentproduksjon.v2.DokumentproduksjonConsumer;
import no.nav.foreldrepenger.melding.dokumentproduksjon.v2.SoapWebServiceFeil;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Dokumentbestillingsinformasjon;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentResponse;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.sak.v1.SakClient;
import no.nav.vedtak.felles.integrasjon.sak.v1.SakJson;

@ApplicationScoped
public class DokprodBrevproduksjonTjeneste implements BrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokprodBrevproduksjonTjeneste.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokumentproduksjonConsumer dokumentproduksjonProxyService;
    private SakClient sakRestKlient;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;
    private DokumentdataMapperProvider dokumentdataMapperProvider;

    public DokprodBrevproduksjonTjeneste() {
        // for cdi proxy
    }

    @Inject
    public DokprodBrevproduksjonTjeneste(DokumentproduksjonConsumer dokumentproduksjonProxyService,
            SakClient sakKlient, // TODO: Legg på @Jersey når opprettsak er borte
            DokumentFellesDataMapper dokumentFellesDataMapper,
            DomeneobjektProvider domeneobjektProvider,
            DokumentRepository dokumentRepository,
            DokumentdataMapperProvider dokumentdataMapperProvider) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
        this.sakRestKlient = sakKlient;
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
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
            var saksnummer = bestemSaksnummer(dokumentHendelse.getDokumentMalType(), dokumentFelles.getSaksnummer(),
                    domeneobjektProvider.hentFagsakBackend(behandling).getAktørId());
            Element brevXmlElement = DokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentFelles, dokumentHendelse, behandling, saksnummer);
            dokumentFelles.setBrevData(elementTilString(brevXmlElement));
            opprettAlternativeBrevDataOmNødvendig(dokumentHendelse, behandling, dokumentMal, dokumentFelles);

            final Dokumentbestillingsinformasjon dokumentbestillingsinformasjon = DokumentbestillingMapper.mapFraBehandling(dokumentMal,
                    dokumentFelles, saksnummer, harVedlegg);
            hentJournalpostTittel(DokumentXmlDataMapper.velgDokumentMapper(dokumentMal))
                    .ifPresent(dokumentbestillingsinformasjon::setUstrukturertTittel);

            ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokumentResponse = produserIkkeredigerbartDokument(brevXmlElement,
                    dokumentbestillingsinformasjon);
            if (harVedlegg) {
                JournalpostId journalpostId = new JournalpostId(produserIkkeredigerbartDokumentResponse.getJournalpostId());
                knyttAlleVedleggTilDokument(vedlegg, journalpostId, behandling.getEndretAv());
                ferdigstillForsendelse(journalpostId, behandling.getEndretAv());
                // TODO kanseller forsendelse hvis det feiler
            }

            historikkinnslagList.add(lagHistorikkinnslag(dokumentHendelse, produserIkkeredigerbartDokumentResponse, dokumentMal));
        }

        return historikkinnslagList;
    }

    Saksnummer bestemSaksnummer(DokumentMalType malType, Saksnummer saksnummer, AktørId aktørId) {
        if (Long.parseLong(saksnummer.getVerdi()) < 152000000L)
            return saksnummer;
        try {
            LOGGER.info("FPFORMIDLING SAK slår opp saksnummer {}", saksnummer.getVerdi());
            var sak = sakRestKlient.finnForSaksnummer(saksnummer.getVerdi());
            if (sak.isEmpty()) {
                LOGGER.info("FPFORMIDLING SAK ingen treff på saksnummer {}", saksnummer.getVerdi());
                if (DokumentMalType.INFO_TIL_ANNEN_FORELDER_DOK.equals(malType)) {
                    sak = Optional.ofNullable(opprettArkivsak(saksnummer, aktørId));
                }
            } else {
                LOGGER.info("FPFORMIDLING SAK fant {} for saksnummer {}", sak.get().getId(), saksnummer.getVerdi());
            }
            return sak.map(s -> new Saksnummer(String.valueOf(s.getId()))).orElseThrow();
        } catch (Exception e) {
            LOGGER.info("FPFORMIDLING SAK feil for saksnummer ", e);
            throw new TekniskException("FPFORMIDLING-210632", String.format("Feilmelding fra Sak."), e);
        }
    }

    private void opprettAlternativeBrevDataOmNødvendig(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal,
            DokumentFelles dokumentFelles) {
        if (DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK.equals(dokumentMal)) {
            try {
                DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(DokumentMalType.INNVILGELSE_FORELDREPENGER);
                Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);
                dokumentdata.getFelles().anonymiser();
                dokumentFelles.setAlternativeBrevData(DefaultJsonMapper.toJson(dokumentdata));
            } catch (Exception e) {
                LOGGER.info("Feilet i å lage Dokgen-versjonen av innvilgelse foreldrepenger for bestilling {} og behandling {}",
                        dokumentHendelse.getBestillingUuid(), dokumentHendelse.getBehandlingUuid(), e);
            }
        }
    }

    // TODO: Fjern når infobrev er på dokgen
    public SakJson opprettArkivsak(Saksnummer saksnummer, AktørId aktørId) {
        var request = SakJson.getBuilder()
                .medAktoerId(aktørId.getId())
                .medFagsakNr(saksnummer.getVerdi())
                .medApplikasjon(Fagsystem.FPSAK.getOffisiellKode())
                .medTema("FOR")
                .build();
        LOGGER.info("SAK REST oppretter sak {}", request);
        var sak = sakRestKlient.opprettSak(request);
        LOGGER.info("SAK REST opprettet sak {} for fagsakNr {}", sak.getId(), sak.getFagsakNr());
        return sak;
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
            throw new TekniskException("FPFORMIDLING-316712", String.format("Feil i ferdigstilling av dokument med journalpostId %s", journalpostId), e );
        }
    }

    private void knyttAlleVedleggTilDokument(Collection<InnsynDokument> vedlegg, JournalpostId journalpostId, String endretAv) {
        vedlegg.forEach(v -> knyttVedleggTilForsendelse(journalpostId, v.journalpostId(), v.dokumentId(), endretAv));
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
            throw new TekniskException("FPFORMIDLING-795245", String.format("Feil i knytting av vedlegg til dokument med id %s", dokumentId), e );
        }
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
                .collect(Collectors.toConcurrentMap(InnsynDokument::dokumentId, Function.identity(), (p, q) -> p))
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
            throw new TekniskException("FPFORMIDLING-210631", String.format("Feilmelding fra DokProd."), funksjonellFeil);
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
                .medHistorikkAktør(
                        dokumentHendelse.getHistorikkAktør() != null ? dokumentHendelse.getHistorikkAktør() : HistorikkAktør.VEDTAKSLØSNINGEN)
                .medDokumentMalType(dokumentMal)
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        if (DokumentMalType.OPPHØR_DOK.equals(dokumentMal) && FagsakYtelseType.SVANGERSKAPSPENGER.equals(dokumentHendelse.getYtelseType())) {
            throw new ForhåndsvisningsException("FPFORMIDLING-221007", "Opphørsbrev Svangerskapspenger ikke implementert", "Se rutine for opphør Svangerskapspenger");
        }
        byte[] dokument;
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);

        final DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();
        var saksnummer = bestemSaksnummer(dokumentHendelse.getDokumentMalType(), førsteDokumentFelles.getSaksnummer(),
                domeneobjektProvider.hentFagsakBackend(behandling).getAktørId());
        Element brevXmlElement = DokumentXmlDataMapper.mapTilBrevXml(dokumentMal, førsteDokumentFelles, dokumentHendelse, behandling, saksnummer);

        førsteDokumentFelles.setBrevData(elementTilString(brevXmlElement));
        dokumentRepository.lagre(dokumentData);

        try {
            dokument = forhåndsvis(dokumentMal, brevXmlElement);
        } catch (IntegrasjonException e) {
            if (!SoapWebServiceFeil.DOKPROD_FEIL_INNHOLD.equals(e.getKode())) throw e;
            if (DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK.equals(dokumentMal) && FagsakYtelseType.FORELDREPENGER.equals(dokumentHendelse.getYtelseType())) {
                throw new TekniskException("FPFORMIDLING-221008", "Feil ved produksjon av forhåndsvisning - se over uttaksperioder/årsakskoder", e.getCause());
            } else {
                throw new TekniskException("FPFORMIDLING-221009",
                        String.format("Forhåndsvisning av %s ikke implementert eller det er mangler i data", dokumentMal.getNavn()), e.getCause());
            }
        }

        if (dokument.length == 0) {
            LOGGER.error("Klarte ikke hente behandling: {}", behandling.getUuid());
            throw new TekniskException("FPFORMIDLING-221005",
                    String.format("Klarte ikke hente forhåndvise mal %s for behandling %s.", dokumentMal.getKode(), behandling.getUuid().toString()));
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return dokument;
    }

    private byte[] forhåndsvis(DokumentMalType dokumentMal, Element brevXmlElement) {
        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
        produserDokumentutkastRequest.setDokumenttypeId(dokumentMal.getDokSysKode().getKode());
        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService
                .produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            return produserDokumentutkastResponse.getDokumentutkast();// $NON-NLS-1$
        }
        return new byte[0];
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

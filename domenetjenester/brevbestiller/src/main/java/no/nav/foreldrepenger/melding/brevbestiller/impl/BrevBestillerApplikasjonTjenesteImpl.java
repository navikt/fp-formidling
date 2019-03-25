package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.XmlUtil.elementTilString;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.brevbestiller.BrevbestillerFeil;
import no.nav.foreldrepenger.melding.brevbestiller.DokumentbestillingMapper;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dtomapper.DtoTilDomeneobjektMapper;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Dokumentbestillingsinformasjon;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentResponse;
import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonConsumer;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrevBestillerApplikasjonTjenesteImpl.class);

    private DokumentproduksjonConsumer dokumentproduksjonProxyService;
    private DokumentXmlDataMapper dokumentXmlDataMapper;
    private DokumentMalUtreder dokumentMalUtreder;
    private BehandlingRestKlient behandlingRestKlient;
    private DokumentbestillingMapper dokumentbestillingMapper;
    private DtoTilDomeneobjektMapper dtoTilDomeneobjektMapper;

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerApplikasjonTjenesteImpl(DokumentproduksjonConsumer dokumentproduksjonProxyService,
                                                DokumentXmlDataMapper dokumentXmlDataMapper,
                                                DokumentMalUtreder dokumentMalUtreder,
                                                BehandlingRestKlient behandlingRestKlient,
                                                DokumentbestillingMapper dokumentbestillingMapper,
                                                DtoTilDomeneobjektMapper dtoTilDomeneobjektMapper) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
        this.dokumentXmlDataMapper = dokumentXmlDataMapper;
        this.dokumentMalUtreder = dokumentMalUtreder;
        this.behandlingRestKlient = behandlingRestKlient;
        this.dokumentbestillingMapper = dokumentbestillingMapper;
        this.dtoTilDomeneobjektMapper = dtoTilDomeneobjektMapper;
    }

    @Override
    public DokumentHistorikkinnslag bestillBrev(DokumentHendelse dokumentHendelse) {
        BehandlingDto behandlingDto = hentBehandlingFraFpsak(dokumentHendelse.getBehandlingId());
        Behandling behandling = new Behandling(behandlingDto);
        DokumentMalType dokumentMal = dokumentMalUtreder.utredDokumentmal(behandling, dokumentHendelse);
        DokumentFelles dokumentFelles = lagDokumentFelles(dokumentMal, behandling.getId());
        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentFelles, dokumentHendelse, behandling);
        final Dokumentbestillingsinformasjon dokumentbestillingsinformasjon = dokumentbestillingMapper.mapFraBehandling(dokumentMal,
                dokumentFelles, false);

        ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokumentResponse = produserIkkeredigerbartDokument(brevXmlElement, dokumentbestillingsinformasjon);
        return lagHistorikkinnslag(dokumentHendelse, produserIkkeredigerbartDokumentResponse, dokumentMal, elementTilString(brevXmlElement));
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
                                                         DokumentMalType dokumentMal,
                                                         String xml) {
        return DokumentHistorikkinnslag.builder()
                .medBehandlingId(dokumentHendelse.getBehandlingId())
                .medHendelseId(dokumentHendelse.getId())
                .medJournalpostId(new JournalpostId(response.getJournalpostId()))
                .medDokumentId(response.getDokumentId())
                .medHistorikkAktør(dokumentHendelse.getHistorikkAktør() != null ? dokumentHendelse.getHistorikkAktør() : HistorikkAktør.VEDTAKSLØSNINGEN)
                .medDokumentMalType(dokumentMal)
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .medXml(xml)
                .build();
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelseDto hendelseDto) {
        byte[] dokument = null;

        //TODO duplisert kode, vurder å lage tjeneste
        DokumentHendelse hendelse = dtoTilDomeneobjektMapper.fraDto(hendelseDto);
        BehandlingDto behandlingDto = hentBehandlingFraFpsak(hendelse.getBehandlingId());
        Behandling behandling = new Behandling(behandlingDto);

        //TODO: Map fpsak data til formidling format
        DokumentMalType dokumentMal = dokumentMalUtreder.utredDokumentmal(behandling, hendelse);

        //Map data til DokumentFelles
        DokumentFelles dokumentFelles = lagDokumentFelles(dokumentMal, behandling.getId());

        //TODO: Map formidling data to xml elements
        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentFelles, hendelse, behandling);

        dokument = forhåndsvis(dokumentMal, brevXmlElement);
        if (dokument == null) {
            LOGGER.error("Klarte ikke hente behandling: {}", behandling.getId());
            throw BrevbestillerFeil.FACTORY.klarteIkkeForhåndvise(dokumentMal.getKode(), behandling.getId()).toException();
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandlingDto.getId());
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

    private BehandlingDto hentBehandlingFraFpsak(long behandlingId) {
        final Optional<BehandlingDto> behandlingInfo = behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingId));
        if (behandlingInfo.isPresent()) {
            return behandlingInfo.get();
        }
        LOGGER.error("Klarte ikke hente behandling: {}", behandlingId);
        throw BrevbestillerFeil.FACTORY.klarteIkkeHenteBehandling(behandlingId).toException();
    }

    private DokumentFelles lagDokumentFelles(DokumentMalType dokumentMalType, Long behandlingId) {
        return DokumentFelles.builder(new DokumentData(dokumentMalType, behandlingId))
                .medMottakerId("z991414")
                .medSaksnummer(Saksnummer.arena("123"))
                .medAutomatiskBehandlet(false)
                .medSakspartId("z991416")
                .medSakspartNavn("test")
                .medNavnAvsenderEnhet("test")
                .medKontaktTelefonNummer("123")
                .medReturadresse(lagDokumentAdresse())
                .medPostadresse(lagDokumentAdresse())
                .medDokumentDato(LocalDate.now())
                .medMottakerAdresse(lagDokumentAdresse())
                .medMottakerNavn("test")
                .medSpråkkode(Språkkode.nb)
                .build();
    }

    private DokumentAdresse lagDokumentAdresse() {
        return new DokumentAdresse.Builder()
                .medAdresselinje1("testadresse 12A")
                .medLand(Landkoder.NOR.getKode())
                .medMottakerNavn("Testmottaker")
                .medPostNummer("1990")
                .medPoststed("oLsO")
                .build();
    }
}

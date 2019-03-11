package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Address;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonConsumer;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrevBestillerApplikasjonTjenesteImpl.class);
    private DokumentproduksjonConsumer dokumentproduksjonProxyService;
    private DokumentXmlDataMapper dokumentXmlDataMapper;
    private DokumentMalUtreder dokumentMalUtreder;
    private KodeverkRepository kodeverkRepository;
    private DokumentRepository dokumentRepository;

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerApplikasjonTjenesteImpl(DokumentproduksjonConsumer dokumentproduksjonProxyService,
                                                DokumentXmlDataMapper dokumentXmlDataMapper,
                                                DokumentMalUtreder dokumentMalUtreder,
                                                KodeverkRepository kodeverkRepository,
                                                DokumentRepository dokumentRepository) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
        this.dokumentXmlDataMapper = dokumentXmlDataMapper;
        this.dokumentMalUtreder = dokumentMalUtreder;
        this.kodeverkRepository = kodeverkRepository;
        this.dokumentRepository = dokumentRepository;
    }

    @Override
    public void bestillBrev(DokumentHendelse dokumentHendelse) {
        //hent behandling her
        Behandling behandling = new Behandling(null);
        dokumentMalUtreder.utredDokumentmal(behandling, dokumentHendelse);
    }

    @Override
    public byte[] forhandsvisBrev(BehandlingDto behandlingDto, DokumentHendelseDto hendelseDto) {
        byte[] dokument = null;

        //TODO duplisert kode, vurder å lage tjeneste
        DokumentHendelse hendelse = fraDto(hendelseDto);

        //TODO: Map fpsak data til formidling format
//        final DokumentData dokumentData = dokumentDataTjeneste.hentDokumentData(dokumentDataId);
        Behandling behandling = new Behandling(behandlingDto);

        //TODO: Sjekk hvis mulighet for flere addresser
        if (behandlingDto.getPersonopplysningDto() != null) {
            Address address = new Address(behandlingDto.getPersonopplysningDto().getAdresser().get(0));
        }
        DokumentMalType dokumentMal = dokumentMalUtreder.utredDokumentmal(behandling, hendelse);


        DokumentFelles dokumentFelles = lagDokumentFelles(dokumentMal, behandling.getId());
        //TODO: Map formidling data to xml elements
        //TODO Bruk likegjerne hendelseobjektet
        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentMal, dokumentFelles, hendelseDto, behandling);

        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
        produserDokumentutkastRequest.setDokumenttypeId(dokumentMal.getDoksysKode());
        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService.produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            dokument = produserDokumentutkastResponse.getDokumentutkast();
            LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandlingDto.getId()); //$NON-NLS-1$
        }
        return dokument;
    }

    private DokumentFelles lagDokumentFelles(DokumentMalType dokumentMalType, Long behandlingId) {
        return DokumentFelles.builder(new DokumentData(dokumentMalType, behandlingId))
                .medMottakerId("123")
                .medSaksnummer(Saksnummer.arena("123"))
                .medAutomatiskBehandlet(false)
                .medSakspartId("123")
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

    private DokumentHendelse fraDto(DokumentHendelseDto hendelseDto) {
        //TODO putt bare ett sted!
        //TODO ha alle feltene..
        //Ny modul under domenetjenester? :) Hva skal den hete..?
        return new DokumentHendelse.Builder()
                .medBehandlingId(hendelseDto.getBehandlingId())
                .medBehandlingType(utledBehandlingType(hendelseDto.getBehandlingType()))
                .medYtelseType(utledYtelseType(hendelseDto.getYtelseType()))
                .medFritekst(hendelseDto.getFritekst())
                .medTittel(hendelseDto.getTittel())
                .medDokumentMalType(utleddokumentMalType(hendelseDto.getDokumentMal()))
                .build();
    }

    private FagsakYtelseType utledYtelseType(String ytelseType) {
        if (StringUtils.nullOrEmpty(ytelseType)) {
            return null;
        }
        return kodeverkRepository.finn(FagsakYtelseType.class, ytelseType);
    }

    private BehandlingType utledBehandlingType(String behandlingType) {
        if (StringUtils.nullOrEmpty(behandlingType)) {
            return null;
        }
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }

    private DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (StringUtils.nullOrEmpty(dokumentmal)) {
            return null;
        }
        return dokumentRepository.hentDokumentMalType(dokumentmal);
    }
}

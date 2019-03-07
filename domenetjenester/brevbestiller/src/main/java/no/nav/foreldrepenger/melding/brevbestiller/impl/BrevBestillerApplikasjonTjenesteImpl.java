package no.nav.foreldrepenger.melding.brevbestiller.impl;

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
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
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
        Address address = new Address(behandlingDto.getPersonopplysningDto().getAdresser().get(0));


        DokumentData dokumentData = null;
        if (dokumentData == null) {
            return null;
        }
        //TODO: Map formidling data to xml elements
        Element brevXmlElement = dokumentXmlDataMapper.mapTilBrevXml(dokumentData, dokumentData.getFørsteDokumentFelles(), hendelseDto);

        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
        produserDokumentutkastRequest.setDokumenttypeId(dokumentData.getDokumentMalType().getDoksysKode());
        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService.produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            dokument = produserDokumentutkastResponse.getDokumentutkast();
            LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentData.getDokumentMalType().getKode(), behandlingDto.getId()); //$NON-NLS-1$
        }
        return dokument;
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

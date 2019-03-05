package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Address;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.vedtak.felles.integrasjon.dokument.produksjon.DokumentproduksjonConsumer;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrevBestillerApplikasjonTjenesteImpl.class);
    private DokumentproduksjonConsumer dokumentproduksjonProxyService;

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public BrevBestillerApplikasjonTjenesteImpl(DokumentproduksjonConsumer dokumentproduksjonProxyService) {
        this.dokumentproduksjonProxyService = dokumentproduksjonProxyService;
    }

    @Override
    public byte[] forhandsvisBrev(BehandlingDto behandlingDto) {
        byte[] dokument = null;

        //TODO: Map fpsak data til formidling format
//        final DokumentData dokumentData = dokumentDataTjeneste.hentDokumentData(dokumentDataId);
        Behandling behandling = new Behandling(behandlingDto);

        //TODO: Sjekk hvis mulighet for felere addresser
        Address address = new Address(behandlingDto.getPersonopplysningDto().getAdresser().get(0));


        //TODO: Map formidling data to xml elements
//        Element brevXmlElement = dokumentToBrevDataMapper.mapTilBrevdata(dokumentData, dokumentData.getFørsteDokumentFelles());

        ProduserDokumentutkastRequest produserDokumentutkastRequest = new ProduserDokumentutkastRequest();
//        produserDokumentutkastRequest.setDokumenttypeId(dokumentData.getDokumentMalType().getDoksysKode());
//        produserDokumentutkastRequest.setBrevdata(brevXmlElement);

        ProduserDokumentutkastResponse produserDokumentutkastResponse = dokumentproduksjonProxyService.produserDokumentutkast(produserDokumentutkastRequest);
        if (produserDokumentutkastResponse != null && produserDokumentutkastResponse.getDokumentutkast() != null) {
            dokument = produserDokumentutkastResponse.getDokumentutkast();
            //TODO: Legg i database, trenger vi det?
//            dokumentForhandsvist(dokumentDataId);
//            LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentData.getDokumentMalType().getKode(), dokumentData.getBehandling().getId()); //$NON-NLS-1$
        }

        return dokument;
    }
}

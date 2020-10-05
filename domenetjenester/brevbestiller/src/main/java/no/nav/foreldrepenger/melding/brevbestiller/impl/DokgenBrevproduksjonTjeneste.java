package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.BrevbestillerFeil;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.dokumentdata.BestillingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.DokgenRestKlient;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste implements BrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokumentRepository dokumentRepository;
    private DokgenRestKlient dokgenRestKlient;

    DokgenBrevproduksjonTjeneste() {
        // CDI
    }

    @Inject
    public DokgenBrevproduksjonTjeneste(DokumentFellesDataMapper dokumentFellesDataMapper,
                                        DokumentRepository dokumentRepository,
                                        DokgenRestKlient dokgenRestKlient) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.dokumentRepository = dokumentRepository;
        this.dokgenRestKlient = dokgenRestKlient;
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        DokumentdataMapper dokumentdataMapper = velgDokumentMapper(dokumentMal);
        Optional<byte[]> brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), dokumentdataMapper.mapTilDokumentdata(førsteDokumentFelles, dokumentHendelse, behandling));
        if (brev.isEmpty()) {
            throw BrevbestillerFeil.FACTORY.klarteIkkeForhåndvise(dokumentMal.getKode(), behandling.getUuid().toString()).toException();
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return brev.get();
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.BESTILL);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);

        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            DokumentdataMapper dokumentdataMapper = velgDokumentMapper(dokumentMal);
            Optional<byte[]> brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling));
        }
        // TODO: Journalføring, distribuering, historikkinnslag, støtte for vedlegg, lagre JSON-serialisert info i "brev_data" eller liknende der XML er lagret i dag?
        return Collections.emptyList();
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }

    private DokumentdataMapper velgDokumentMapper(DokumentMalType dokumentMalType) {
        return DokumentMalTypeRef.Lookup.find(DokumentdataMapper.class, dokumentMalType.getKode()).orElseThrow();
    }
}

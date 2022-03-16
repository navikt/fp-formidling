package no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.foreldrepenger.fpformidling.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.fpsak.Behandlinger;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.historikk.v1.HistorikkInnslagV1;

@ApplicationScoped
public class KvitteringTjeneste {

    private static final Logger log = LoggerFactory.getLogger(KvitteringTjeneste.class);

    private static final ObjectMapper mapper = getObjectMapper();

    private DokumentHistorikkinnslagProducer meldingProducer;
    private Behandlinger behandlinger;

    public KvitteringTjeneste() {
        //CDI
    }

    @Inject
    public KvitteringTjeneste(DokumentHistorikkinnslagProducer dokumentMeldingProducer,
                              Behandlinger behandlinger) {
        this.meldingProducer = dokumentMeldingProducer;
        this.behandlinger = behandlinger;
    }

    @Deprecated(forRemoval = true) // Use sendKvittering(DokumentProdusertDto)
    public void publiserHistorikk(DokumentHistorikkinnslag historikkInnslag) {
        if (!Environment.current().isProd()) {
            var kvittering = new DokumentProdusertDto(
                    historikkInnslag.getBehandlingUuid(),
                    historikkInnslag.getHistorikkUuuid(),
                    historikkInnslag.getDokumentMalType().getKode(),
                    historikkInnslag.getJournalpostId().getVerdi(),
                    historikkInnslag.getDokumentId());
            sendKvittering(kvittering);
        } else {
            HistorikkInnslagV1 historikk = HistorikkTilDtoMapper.mapHistorikkinnslag(historikkInnslag);
            publiserHistorikk(historikk, historikkInnslag.getHendelseId());
        }
    }

    public void sendKvittering(DokumentProdusertDto kvittering) {
        behandlinger.kvitterDokument(kvittering);
    }

    private void publiserHistorikk(HistorikkInnslagV1 jsonHistorikk, long hendelseId) {
        String serialisertJson = serialiser(jsonHistorikk);
        meldingProducer.sendJson(serialisertJson);
        log.info("Publisert historikk for hendelse: {}", hendelseId);
    }

    private String serialiser(HistorikkInnslagV1 historikk) {
        try {
            return mapper.writeValueAsString(historikk);
        } catch (IOException e) {
            log.error("Klarte ikke å serialisere historikkinnslag");
            return null;
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

}

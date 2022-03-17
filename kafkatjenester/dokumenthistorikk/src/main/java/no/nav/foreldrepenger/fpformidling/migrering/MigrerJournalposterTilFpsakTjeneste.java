package no.nav.foreldrepenger.fpformidling.migrering;

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
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkRepository;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;

@ApplicationScoped
public class MigrerJournalposterTilFpsakTjeneste {

    private static final Logger log = LoggerFactory.getLogger(MigrerJournalposterTilFpsakTjeneste.class);

    private static final ObjectMapper mapper = getObjectMapper();

    private DokumentHistorikkinnslagProducer meldingProducer;
    private HistorikkRepository historikkRepository;

    public MigrerJournalposterTilFpsakTjeneste() {
        //CDI
    }

    @Inject
    public MigrerJournalposterTilFpsakTjeneste(DokumentHistorikkinnslagProducer dokumentMeldingProducer) {
        this.meldingProducer = dokumentMeldingProducer;
    }

    public void migrerHistorikk() {
        historikkRepository.all().stream().map(this::toDto).forEach(this::send);
    }

    private DokumentProdusertDto toDto(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return new DokumentProdusertDto(
                dokumentHistorikkinnslag.getBehandlingUuid(),
                dokumentHistorikkinnslag.getHistorikkUuuid(),
                dokumentHistorikkinnslag.getDokumentMalType().getKode(),
                dokumentHistorikkinnslag.getJournalpostId().getVerdi(),
                dokumentHistorikkinnslag.getDokumentId()
        );
    }

    private void send(DokumentProdusertDto historikk) {
        meldingProducer.sendJson(serialiser(historikk));
    }

    private String serialiser(DokumentProdusertDto historikk) {
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

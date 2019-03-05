package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import static no.nav.foreldrepenger.melding.kafkatjenester.util.Serialiseringsverktøy.getObjectMapper;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.vedtak.felles.kafka.MeldingProducer;

@ApplicationScoped
public class DokumentHistorikkTjeneste {

    private static final Logger log = LoggerFactory.getLogger(DokumentHistorikkTjeneste.class);

    private static final ObjectMapper mapper = getObjectMapper();

    private MeldingProducer meldingProducer;

    public DokumentHistorikkTjeneste() {
        //CDI
    }

    @Inject
    public DokumentHistorikkTjeneste(DokumentHistorikkMeldingProducer dokumentMeldingProducer) {
        this.meldingProducer = dokumentMeldingProducer;
    }


    public void publiserHistorikk(DokumentHistorikkDto jsonHistorikk) {
        String serialisertJson = serialiser(jsonHistorikk);
        meldingProducer.sendJson(serialisertJson);
        log.info("Publisert historikk: {}", serialisertJson);
    }

    private String serialiser(DokumentHistorikkDto historikk) {
        try {
            return mapper.writeValueAsString(historikk);
        } catch (IOException e) {
            log.error("Klarte ikke å serialisere historikkinnslag");
            return null;
        }
    }

}

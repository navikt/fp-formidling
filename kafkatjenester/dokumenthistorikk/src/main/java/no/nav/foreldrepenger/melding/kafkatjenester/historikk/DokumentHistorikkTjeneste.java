package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.kafkatjenester.felles.util.Serialiseringsverktøy;

@ApplicationScoped
public class DokumentHistorikkTjeneste {

    private static final Logger log = LoggerFactory.getLogger(DokumentHistorikkTjeneste.class);

    private static final ObjectMapper mapper = Serialiseringsverktøy.getObjectMapper();

    private DokumentHistorikkMeldingProducer meldingProducer;

    public DokumentHistorikkTjeneste() {
        //CDI
    }

    @Inject
    public DokumentHistorikkTjeneste(DokumentHistorikkMeldingProducer dokumentMeldingProducer) {
        this.meldingProducer = dokumentMeldingProducer;
    }


    public void publiserHistorikk(DokumentHistorikkinnslag historikkInnslag) {
        DokumentHistorikkDto historikk = new DokumentHistorikkDto(historikkInnslag);
        publiserHistorikk(historikk, historikkInnslag.getHendelseId());
    }

    void publiserHistorikk(DokumentHistorikkDto jsonHistorikk, long hendelseId) {
        String serialisertJson = serialiser(jsonHistorikk);
        meldingProducer.sendJson(serialisertJson);
        log.info("Publisert historikk for hendelse: {} : {}", hendelseId, serialisertJson);
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

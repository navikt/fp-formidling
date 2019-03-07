package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import static no.nav.foreldrepenger.melding.kafkatjenester.util.Serialiseringsverktøy.getObjectMapper;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.vedtak.felles.kafka.MeldingProducer;

@ApplicationScoped
public class DokumentHistorikkTjeneste {

    private static final Logger log = LoggerFactory.getLogger(DokumentHistorikkTjeneste.class);

    private static final ObjectMapper mapper = getObjectMapper();

    private MeldingProducer meldingProducer;
    private HistorikkRepository historikkRepository;

    public DokumentHistorikkTjeneste() {
        //CDI
    }

    @Inject
    public DokumentHistorikkTjeneste(DokumentHistorikkMeldingProducer dokumentMeldingProducer,
                                     HistorikkRepository historikkRepository) {
        this.meldingProducer = dokumentMeldingProducer;
        this.historikkRepository = historikkRepository;
    }


    public void lagreOgPubliserHistorikk(DokumentHendelse hendelse) {
        DokumentHistorikkinnslag historikkInnslag = lagHistorikkinnslag(hendelse);
        historikkRepository.lagre(historikkInnslag);
        DokumentHistorikkDto historikk = new DokumentHistorikkDto(historikkInnslag);
        publiserHistorikk(historikk);
    }

    private DokumentHistorikkinnslag lagHistorikkinnslag(DokumentHendelse dokumentHendelse) {
        //TODO
        return DokumentHistorikkinnslag.builder()
                .medBehandlingId(dokumentHendelse.getBehandlingId())
                .medJournalpostId(lagJournalpost())
                .medDokumentId("123")
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .medDokumentMalType(dokumentHendelse.getDokumentMalType())
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
    }

    private JournalpostId lagJournalpost() {
        return new JournalpostId("TODO");
    }

    void publiserHistorikk(DokumentHistorikkDto jsonHistorikk) {
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

package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakStatus;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.AktiverContextOgTransaksjon;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;

@ApplicationScoped
@AktiverContextOgTransaksjon
public class KafkaReader {

    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private JsonHendelseHandler jsonHendelseHandler;
    private StringBuilder feilmelding;
    private HendelseRepository hendelseRepository;


    @Inject
    public KafkaReader(JsonHendelseHandler jsonOppgaveHandler,
                       HendelseRepository hendelseRepository) {
        this.jsonHendelseHandler = jsonOppgaveHandler;
        this.hendelseRepository = hendelseRepository;
    }

    public KafkaReader() {
        //CDI
    }

    void prosesser(String melding) {
        log.info("Mottatt melding med start : {}", melding.substring(0, Math.min(melding.length() - 1, 1000)));
        feilmelding = new StringBuilder();
        try {
            DokumentbestillingV1 jsonHendelse = deserialiser(melding, DokumentbestillingV1.class);
            if (jsonHendelse != null) {
                jsonHendelseHandler.prosesser(jsonHendelse);
                return;
            }
            loggFeiletDeserialisering(melding);
            log.error("Klarte ikke å deserialisere meldingen");
        } catch (Exception tekniskException) {
            feilmelding.append(tekniskException.getClass().getSimpleName()).append(": ");
            feilmelding.append(tekniskException.getMessage());
            log.warn("Feil ved deserialisering lagt til i logg", tekniskException);
            loggFeiletDeserialisering(melding);
        }
    }

    private <T> T deserialiser(String melding, Class<T> klassetype) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return mapper.readValue(melding, klassetype);
        } catch (IOException e) {
            log.info("Klarte ikke å deserialisere basert på Objektet med klassetype " + klassetype + " melding: " + melding, e);
            feilmelding.append(e.getMessage());
            return null;
        }
    }

    private void loggFeiletDeserialisering(String melding) {
        hendelseRepository.lagre(new EventmottakFeillogg(melding, EventmottakStatus.FEILET, LocalDateTime.now(), feilmelding.toString()));
    }

}

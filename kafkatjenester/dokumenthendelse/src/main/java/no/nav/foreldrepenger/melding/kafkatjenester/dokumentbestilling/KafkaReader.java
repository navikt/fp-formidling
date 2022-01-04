package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import static java.lang.String.format;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakStatus;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;

@ApplicationScoped
@ActivateRequestContext
@Transactional
public class KafkaReader {
    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private HendelseHandler hendelseHandler;
    private StringBuilder feilmelding;
    private HendelseRepository hendelseRepository;

    @Inject
    public KafkaReader(HendelseHandler jsonOppgaveHandler,
                       HendelseRepository hendelseRepository) {
        this.hendelseHandler = jsonOppgaveHandler;
        this.hendelseRepository = hendelseRepository;
    }

    public KafkaReader() {
        //CDI
    }

    void prosesser(String melding) {
        feilmelding = new StringBuilder();
        try {
            DokumentbestillingV1 jsonHendelse = deserialiser(melding, DokumentbestillingV1.class);
            if (jsonHendelse != null) {
                DokumentHendelse hendelse = DokumentHendelseDtoMapper.mapDokumentHendelseFraDtoForKafka(jsonHendelse);
                hendelseHandler.prosesser(hendelse);
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
            log.info(format("Klarte ikke å deserialisere basert på Objektet med klassetype %s melding: %s", klassetype, melding), e);
            feilmelding.append(e.getMessage());
            return null;
        }
    }

    private void loggFeiletDeserialisering(String melding) {
        hendelseRepository.lagre(new EventmottakFeillogg(melding, EventmottakStatus.FEILET, LocalDateTime.now(), feilmelding.toString()));
    }
}

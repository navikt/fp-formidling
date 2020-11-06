package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.foreldrepenger.melding.dbstoette.EntityManagerAwareExtension;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;
import no.nav.foreldrepenger.melding.hendelse.HendelseHandler;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kafkatjenester.felles.util.Serialiseringsverktøy;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.felles.dokumentbestilling.kodeverk.FagsakYtelseType;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.testutilities.db.Repository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerAwareExtension.class)
public class KafkaReaderTest {

    @Mock
    private ProsessTaskRepository prosessTaskRepository;
    private KafkaReader kafkaReader;
    private Repository repository;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        var hendelseRepository = new HendelseRepository(entityManager);
        var hendelseHandler = new HendelseHandler(hendelseRepository, prosessTaskRepository);
        kafkaReader = new KafkaReader(hendelseHandler, hendelseRepository);
        repository = new Repository(entityManager);

    }

    @Test
    public void normal_melding_skal_prossesseres_ok() {
        String json = serialiser(lagOkDto());
        kafkaReader.prosesser(json);
        assertThat(repository.hentAlle(EventmottakFeillogg.class)).hasSize(0);
    }

    @Test
    public void ugyldig_melding_skal_logges() {
        kafkaReader.prosesser("Bare tull");
        assertThat(repository.hentAlle(EventmottakFeillogg.class)).hasSize(1);
        assertThat(repository.hentAlle(EventmottakFeillogg.class).get(0).getMelding()).isEqualToIgnoringCase("Bare tull");
    }

    private String serialiser(DokumentbestillingV1 dto) {
        try {
            return Serialiseringsverktøy.getObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private DokumentbestillingV1 lagOkDto() {
        DokumentbestillingV1 dto = new DokumentbestillingV1();
        dto.setBehandlingUuid(UUID.randomUUID());
        dto.setDokumentbestillingUuid(UUID.randomUUID());
        dto.setDokumentMal(DokumentMalType.AVSLAGSVEDTAK_DOK.getKode());
        dto.setFritekst("123");
        dto.setHistorikkAktør(HistorikkAktør.BESLUTTER.getKode());
        dto.setYtelseType(FagsakYtelseType.FORELDREPENGER);
        return dto;
    }
}

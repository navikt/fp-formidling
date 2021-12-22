package no.nav.foreldrepenger.melding.kafkatjenester.dokumentbestilling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.foreldrepenger.melding.dbstoette.JpaExtension;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;
import no.nav.foreldrepenger.melding.hendelse.HendelseHandler;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kafkatjenester.felles.util.Serialiseringsverktøy;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.felles.dokumentbestilling.kodeverk.FagsakYtelseType;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
public class KafkaReaderTest {

    @Mock
    private ProsessTaskTjeneste taskTjeneste;
    private KafkaReader kafkaReader;
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        var hendelseRepository = new HendelseRepository(entityManager);
        var hendelseHandler = new HendelseHandler(hendelseRepository, taskTjeneste);
        kafkaReader = new KafkaReader(hendelseHandler, hendelseRepository);
        this.entityManager = entityManager;
    }

    @Test
    public void normal_melding_skal_prossesseres_ok() {
        String json = serialiser(lagOkDto());
        kafkaReader.prosesser(json);
        assertThat(hentAlleEventerFraFeillogg()).hasSize(0);
    }

    @Test
    public void ugyldig_melding_skal_logges() {
        kafkaReader.prosesser("Bare tull");
        assertThat(hentAlleEventerFraFeillogg()).hasSize(1);
        assertThat(hentAlleEventerFraFeillogg().get(0).getMelding()).isEqualToIgnoringCase("Bare tull");
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
        dto.setDokumentMal(DokumentMalType.FORELDREPENGER_INNVILGELSE.getKode());
        dto.setFritekst("123");
        dto.setHistorikkAktør(HistorikkAktør.BESLUTTER.getKode());
        dto.setYtelseType(FagsakYtelseType.FORELDREPENGER);
        return dto;
    }

    private List<EventmottakFeillogg> hentAlleEventerFraFeillogg() {
        CriteriaQuery<EventmottakFeillogg> criteria = entityManager.getCriteriaBuilder().createQuery(EventmottakFeillogg.class);
        criteria.select(criteria.from(EventmottakFeillogg.class));
        return entityManager.createQuery(criteria).getResultList();
    }
}

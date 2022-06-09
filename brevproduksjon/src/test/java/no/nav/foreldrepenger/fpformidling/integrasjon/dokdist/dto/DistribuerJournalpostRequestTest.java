package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.vedtak.mapper.json.DefaultJsonMapper;

class DistribuerJournalpostRequestTest {

    @Test
    void serdeTest() {
        var kjernetid = Distribusjonstidspunkt.KJERNETID;
        var distribusjonstype = Distribusjonstype.VEDTAK;
        var journalpostId = "12345";
        var batchId = "67890";
        var bestillendeFagsystem = "FPSAK";
        var dokumentProdApp = "FFF";
        var testRequest = new DistribuerJournalpostRequest(journalpostId, batchId, bestillendeFagsystem, dokumentProdApp, distribusjonstype, kjernetid);

        var serieliser = DefaultJsonMapper.toJson(testRequest);

        assertThat(serieliser).contains("distribusjonstype").contains("distribusjonstidspunkt");

        var deserialisertRequest = DefaultJsonMapper.fromJson(serieliser, DistribuerJournalpostRequest.class);

        assertThat(deserialisertRequest.distribusjonstidspunkt()).isEqualTo(kjernetid);
        assertThat(deserialisertRequest.distribusjonstype()).isEqualTo(distribusjonstype);
        assertThat(deserialisertRequest.journalpostId()).isEqualTo(journalpostId);
        assertThat(deserialisertRequest.batchId()).isEqualTo(batchId);
        assertThat(deserialisertRequest.bestillendeFagsystem()).isEqualTo(bestillendeFagsystem);
        assertThat(deserialisertRequest.dokumentProdApp()).isEqualTo(dokumentProdApp);
    }

    @Test
    void serdeMedNullVerdierSomSkalIgnoreresTest() {
        var journalpostId = "12345";
        var batchId = "67890";
        var bestillendeFagsystem = "FPSAK";
        var dokumentProdApp = "FFF";
        var testRequest = new DistribuerJournalpostRequest(journalpostId, batchId, bestillendeFagsystem, dokumentProdApp, null, null);

        var serieliser = DefaultJsonMapper.toJson(testRequest);

        assertThat(serieliser).doesNotContain("distribusjonstype");
        assertThat(serieliser).doesNotContain("distribusjonstidspunkt");

        var deserialisertRequest = DefaultJsonMapper.fromJson(serieliser, DistribuerJournalpostRequest.class);

        assertThat(deserialisertRequest.distribusjonstidspunkt()).isNull();
        assertThat(deserialisertRequest.distribusjonstype()).isNull();
        assertThat(deserialisertRequest.journalpostId()).isEqualTo(journalpostId);
        assertThat(deserialisertRequest.batchId()).isEqualTo(batchId);
        assertThat(deserialisertRequest.bestillendeFagsystem()).isEqualTo(bestillendeFagsystem);
        assertThat(deserialisertRequest.dokumentProdApp()).isEqualTo(dokumentProdApp);
    }
}
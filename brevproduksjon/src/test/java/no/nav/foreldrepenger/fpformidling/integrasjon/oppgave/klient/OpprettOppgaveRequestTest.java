package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.vedtak.mapper.json.DefaultJsonMapper;

class OpprettOppgaveRequestTest {

    @Test
    void serdeTest() {
        var enhetsnr = "2096";
        var journalpostId = "12345";
        var behandlesAvApplikasjon = "FPSAK";
        var saksreferanse = "123456";
        var aktoerId = "34535634534";
        var beskrivelse = "Test oppgave";
        var temagruppe = "FPGRUPPE";
        var tema = "FOR";
        var behandlingstema = "behtema";
        var oppgavetype = "KONS";
        var behandlingstype = "REV";
        var aktivDato = LocalDate.now();

        var testRequest = new OpprettOppgaveRequest(enhetsnr,
                enhetsnr,
                journalpostId,
                behandlesAvApplikasjon,
                saksreferanse,
                aktoerId,
                beskrivelse,
                temagruppe,
                tema,
                behandlingstema,
                oppgavetype,
                behandlingstype,
                aktivDato,
                Prioritet.NORM,
                aktivDato.plusDays(1)
        );

        var serieliser = DefaultJsonMapper.toJson(testRequest);

        assertThat(serieliser)
                .contains("tildeltEnhetsnr")
                .contains("opprettetAvEnhetsnr")
                .contains("journalpostId")
                .contains("behandlesAvApplikasjon")
                .contains("saksreferanse")
                .contains("aktoerId")
                .contains("beskrivelse")
                .contains("temagruppe")
                .contains("tema")
                .contains("behandlingstema")
                .contains("oppgavetype")
                .contains("behandlingstype")
                .contains("aktivDato")
                .contains("prioritet")
                .contains("fristFerdigstillelse")
        ;

        var deserialisertRequest = DefaultJsonMapper.fromJson(serieliser, OpprettOppgaveRequest.class);

        assertThat(deserialisertRequest.tildeltEnhetsnr()).isEqualTo(enhetsnr);
        assertThat(deserialisertRequest.opprettetAvEnhetsnr()).isEqualTo(enhetsnr);
        assertThat(deserialisertRequest.journalpostId()).isEqualTo(journalpostId);
        assertThat(deserialisertRequest.behandlesAvApplikasjon()).isEqualTo(behandlesAvApplikasjon);
        assertThat(deserialisertRequest.saksreferanse()).isEqualTo(saksreferanse);
        assertThat(deserialisertRequest.aktoerId()).isEqualTo(aktoerId);
        assertThat(deserialisertRequest.beskrivelse()).isEqualTo(beskrivelse);
        assertThat(deserialisertRequest.temagruppe()).isEqualTo(temagruppe);
        assertThat(deserialisertRequest.tema()).isEqualTo(tema);
        assertThat(deserialisertRequest.behandlingstema()).isEqualTo(behandlingstema);
        assertThat(deserialisertRequest.oppgavetype()).isEqualTo(oppgavetype);
        assertThat(deserialisertRequest.behandlingstype()).isEqualTo(behandlingstype);
        assertThat(deserialisertRequest.aktivDato()).isEqualTo(aktivDato);
        assertThat(deserialisertRequest.prioritet()).isEqualTo(Prioritet.NORM);
        assertThat(deserialisertRequest.fristFerdigstillelse()).isEqualTo(aktivDato.plusDays(1));
    }

}

package no.nav.foreldrepenger.fpformidling.web.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import ch.qos.logback.classic.Level;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.util.MemoryAppender;

@Execution(ExecutionMode.SAME_THREAD)
class GeneralRestExceptionMapperTest {

    private static MemoryAppender logSniffer;

    private final GeneralRestExceptionMapper exceptionMapper = new GeneralRestExceptionMapper();

    @BeforeEach
    void setUp() {
        logSniffer = MemoryAppender.sniff(GeneralRestExceptionMapper.class);
    }

    @AfterEach
    void afterEach() {
        logSniffer.reset();
    }

    @Test
    void skalIkkeMappeManglerTilgangFeil() {
        var response = exceptionMapper.toResponse(manglerTilgangFeil());

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.type()).isEqualTo(FeilType.MANGLER_TILGANG_FEIL);
        assertThat(feilDto.feilmelding()).contains("ManglerTilgangFeilmeldingKode");
        assertThat(logSniffer.search("ManglerTilgangFeilmeldingKode", Level.WARN)).isEmpty();
    }

    @Test
    void skalMappeFunksjonellFeil() {
        var response = exceptionMapper.toResponse(funksjonellFeil());

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("FUNK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en funksjonell feilmelding");
        assertThat(feilDto.feilmelding()).contains("et løsningsforslag");
        assertThat(logSniffer.search("en funksjonell feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    void skalMappeVLException() {
        var response = exceptionMapper.toResponse(tekniskFeil());

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("TEK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en teknisk feilmelding");
        assertThat(logSniffer.search("en teknisk feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    void skalMappeWrappedGenerellFeil() {
        var feilmelding = "en helt generell feil";
        var generellFeil = new RuntimeException(feilmelding);

        var response = exceptionMapper.toResponse(new TekniskException("KODE", "TEKST", generellFeil));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("TEKST");
        assertThat(logSniffer.search("TEKST", Level.WARN)).hasSize(1);
    }

    @Test
    void skalMappeWrappedFeilUtenCause() {
        var feilmelding = "en helt generell feil";

        var response = exceptionMapper.toResponse(new TekniskException("KODE", feilmelding));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(feilmelding);
        assertThat(logSniffer.search(feilmelding, Level.WARN)).hasSize(1);
    }

    @Test
    void skalMappeGenerellFeil() {
        var feilmelding = "en helt generell feil";
        RuntimeException generellFeil = new IllegalArgumentException(feilmelding);

        var response = exceptionMapper.toResponse(generellFeil);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(feilmelding);
        assertThat(logSniffer.search(feilmelding, Level.WARN)).hasSize(1);
    }


    private static FunksjonellException funksjonellFeil() {
        return new FunksjonellException("FUNK_FEIL", "en funksjonell feilmelding", "et løsningsforslag");
    }

    private static TekniskException tekniskFeil() {
        return new TekniskException("TEK_FEIL", "en teknisk feilmelding");
    }

    private static ManglerTilgangException manglerTilgangFeil() {
        return new ManglerTilgangException("MANGLER_TILGANG_FEIL","ManglerTilgangFeilmeldingKode");
    }

}

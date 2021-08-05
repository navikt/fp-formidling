package no.nav.foreldrepenger.melding.web.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import ch.qos.logback.classic.Level;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.log.util.MemoryAppender;

@Execution(ExecutionMode.SAME_THREAD)
public class VLExceptionMapperTest {

    private final VLExceptionMapper vlExceptionMapper = new VLExceptionMapper();
    private MemoryAppender logSniffer;

    @BeforeEach
    public void setUp() {
        logSniffer = MemoryAppender.sniff(VLExceptionMapper.class);
    }

    @AfterEach
    public void afterEach() {
        logSniffer.reset();
    }

    @Test
    public void skalMappeManglerTilgangFeil() {
        VLException manglerTilgangFeil = new ManglerTilgangException("MANGLER_TILGANG_FEIL", "ManglerTilgangFeilmeldingKode");

        Response response = vlExceptionMapper.toResponse(manglerTilgangFeil);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.type()).isEqualTo(FeilType.MANGLER_TILGANG_FEIL);
        assertThat(feilDto.feilmelding()).isEqualTo("MANGLER_TILGANG_FEIL:ManglerTilgangFeilmeldingKode");
        assertThat(logSniffer.search("MANGLER_TILGANG_FEIL:ManglerTilgangFeilmeldingKode", Level.WARN)).isEmpty();
    }

    @Test
    public void skalMappeFunksjonellFeil() {
        VLException funksjonellFeil = new FunksjonellException("FUNK_FEIL", "en funksjonell feilmelding", "et løsningsforslag");

        Response response = vlExceptionMapper.toResponse(funksjonellFeil);

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("FUNK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en funksjonell feilmelding");
        assertThat(feilDto.feilmelding()).contains("et løsningsforslag");
        assertThat(logSniffer.search("en funksjonell feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeVLException() {
        VLException vlException = new TekniskException("TEK_FEIL", "en teknisk feilmelding");

        Response response = vlExceptionMapper.toResponse(vlException);

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("TEK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en teknisk feilmelding");
        assertThat(logSniffer.search("en teknisk feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeWrappedGenerellFeil() {
        String feilmelding = "en helt generell feil";
        RuntimeException generellFeil = new RuntimeException(feilmelding);

        Response response = vlExceptionMapper.toResponse(new TekniskException("KODE", "TEKST", generellFeil));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("TEKST");
        assertThat(logSniffer.search("TEKST", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeWrappedFeilUtenCause() {
        String feilmelding = "en helt generell feil";

        Response response = vlExceptionMapper.toResponse(new TekniskException("KODE", feilmelding));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(feilmelding);
        assertThat(logSniffer.search(feilmelding, Level.WARN)).hasSize(1);
    }
}

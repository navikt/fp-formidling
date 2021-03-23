package no.nav.foreldrepenger.melding.web.app.exceptions;

import ch.qos.logback.classic.Level;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.log.util.MemoryAppender;
import org.jboss.resteasy.spi.ApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
public class GeneralRestExceptionMapperTest {

    private final GeneralRestExceptionMapper generalRestExceptionMapper = new GeneralRestExceptionMapper();
    private MemoryAppender logSniffer;

    @BeforeEach
    public void setUp() {
        logSniffer = MemoryAppender.sniff(GeneralRestExceptionMapper.class);
    }

    @AfterEach
    public void afterEach() {
        logSniffer.reset();
    }

    @Test
    public void skalMappeValideringsfeil() {
        FeltFeilDto feltFeilDto = new FeltFeilDto("Et feltnavn", "En feilmelding");
        Valideringsfeil valideringsfeil = new Valideringsfeil(Collections.singleton(feltFeilDto));

        Response response = generalRestExceptionMapper.toResponse(new ApplicationException(valideringsfeil));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).isEqualTo(
                "FPFORMIDLING-328673:Det oppstod en valideringsfeil på felt [Et feltnavn]. Vennligst kontroller at alle feltverdier er korrekte.");
        assertThat(feilDto.getFeltFeil()).hasSize(1);
        assertThat(feilDto.getFeltFeil().iterator().next()).isEqualTo(feltFeilDto);
    }

    @Test
    public void skalMapperValideringsfeilMedMetainformasjon() {
        FeltFeilDto feltFeilDto = new FeltFeilDto("feltnavn", "feilmelding", "metainformasjon");
        Valideringsfeil valideringsfeil = new Valideringsfeil(Collections.singleton(feltFeilDto));

        Response response = generalRestExceptionMapper.toResponse(new ApplicationException(valideringsfeil));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).isEqualTo(
                "FPFORMIDLING-328673:Det oppstod en valideringsfeil på felt [feltnavn]. Vennligst kontroller at alle feltverdier er korrekte.");
        assertThat(feilDto.getFeltFeil()).hasSize(1);
        assertThat(feilDto.getFeltFeil().iterator().next()).isEqualTo(feltFeilDto);
    }

    @Test
    public void skalMappeManglerTilgangFeil() {
        VLException manglerTilgangFeil = new ManglerTilgangException("MANGLER_TILGANG_FEIL", "ManglerTilgangFeilmeldingKode");

        Response response = generalRestExceptionMapper.toResponse(
                new ApplicationException(manglerTilgangFeil));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getType()).isEqualTo(FeilType.MANGLER_TILGANG_FEIL);
        assertThat(feilDto.getFeilmelding()).isEqualTo("MANGLER_TILGANG_FEIL:ManglerTilgangFeilmeldingKode");
        assertThat(logSniffer.search("MANGLER_TILGANG_FEIL:ManglerTilgangFeilmeldingKode", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeFunksjonellFeil() {
        VLException funksjonellFeil = new FunksjonellException("FUNK_FEIL","en funksjonell feilmelding", "et løsningsforslag");

        Response response = generalRestExceptionMapper.toResponse(
                new ApplicationException(funksjonellFeil));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains("FUNK_FEIL");
        assertThat(feilDto.getFeilmelding()).contains("en funksjonell feilmelding");
        assertThat(feilDto.getFeilmelding()).contains("et løsningsforslag");
        assertThat(logSniffer.search("en funksjonell feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeVLException() {
        VLException vlException = new TekniskException("TEK_FEIL", "en teknisk feilmelding");

        Response response = generalRestExceptionMapper.toResponse(new ApplicationException(vlException));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains("TEK_FEIL");
        assertThat(feilDto.getFeilmelding()).contains("en teknisk feilmelding");
        assertThat(logSniffer.search("en teknisk feilmelding", Level.WARN)).hasSize(1);
    }

    @Test
    public void skalMappeGenerellFeil() {
        String feilmelding = "en helt generell feil";
        RuntimeException generellFeil = new RuntimeException(feilmelding);

        Response response = generalRestExceptionMapper.toResponse(new ApplicationException(generellFeil));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.getFeilmelding()).contains(feilmelding);
        assertThat(logSniffer.search(feilmelding, Level.ERROR)).hasSize(1);
    }
}

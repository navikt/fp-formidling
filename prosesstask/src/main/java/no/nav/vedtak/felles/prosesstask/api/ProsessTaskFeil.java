package no.nav.vedtak.felles.prosesstask.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.jpa.savepoint.SavepointRolledbackException;
import no.nav.vedtak.log.mdc.MDCOperations;

/**
 * Json struktur for feil som kan oppstå. Dupliserer noen properties for enkelthets skyld til senere prosessering.
 * <p>
 * Kan kun gjenskapes som json dersom sisteFeil ble lagret som Json i PROSESS_TASK tabell
 * (dvs. i nyere versjoner &gt;=2.4, gamle versjoner lagrer som flat string).
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class ProsessTaskFeil {

    private static ObjectWriter objectWriter;
    private static ObjectReader objectReader;

    static {
        ObjectMapper om = new ObjectMapper();
        objectWriter = om.writerWithDefaultPrettyPrinter();
        objectReader = om.reader();
    }

    @JsonProperty("exceptionCauseClass")
    private String exceptionCauseClass;

    @JsonProperty("exceptionCauseMessage")
    private String exceptionCauseMessage;

    @JsonProperty("taskName")
    private String taskName;

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("callId")
    private String callId;

    @JsonProperty("feilkode")
    private String feilkode;

    @JsonProperty("feilmelding")
    private String feilmelding;

    @JsonProperty("stacktrace")
    private String stackTrace;

    @JsonProperty("blokkertAv")
    private Long blokkerendeProsessTaskId;

    public ProsessTaskFeil() {
        // default ctor for proxy
    }

    public ProsessTaskFeil(ProsessTaskInfo taskInfo, Feil feil) {
        if (feil != null) {
            Throwable cause = getCause(feil);

            if (cause != null) {
                // bruker her unwrapped cause hvis finnes
                this.exceptionCauseClass = cause.getClass().getName();
                this.exceptionCauseMessage = cause.getMessage();
                this.feilkode = finnFeilkode(cause);
            }
            if (this.feilkode == null) {
                this.feilkode = feil.getKode();
            }

            if (feil.getCause() != null) {
                // her brukes original exception (ikke unwrapped) slik at vi får med hele historikken hvor eksakt dette inntraff
                this.stackTrace = getStacktraceAsString(feil.getCause());// bruker original exception uansett (inkludert wrapping exceptions)
            }

            this.feilmelding = feil.getFeilmelding();
        }

        this.taskName = taskInfo.getTaskType();
        this.taskId = taskInfo.getId() == null ? null : taskInfo.getId().toString();
        this.callId = taskInfo.getPropertyValue(MDCOperations.MDC_CALL_ID);
    }

    public static String readFrom(String str) throws IOException {
        return objectReader.readValue(str);
    }

    public String getExceptionCauseClass() {
        return exceptionCauseClass;
    }

    public String getExceptionCauseMessage() {
        return exceptionCauseMessage;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getCallId() {
        return callId;
    }

    public String getFeilkode() {
        return feilkode;
    }

    public void setFeilkode(String feilkode) {
        this.feilkode = feilkode;
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    private String getStacktraceAsString(Throwable cause) {
        if (cause == null) {
            return null;
        }
        StringWriter sw = new StringWriter(4096);
        PrintWriter pw = new PrintWriter(sw);
        cause.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private Throwable getCause(Feil feil) {
        if (feil == null) {
            return null;
        }
        Throwable cause = feil.getCause();
        if (cause instanceof SavepointRolledbackException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    private String finnFeilkode(Throwable e) {
        return e instanceof VLException
                ? ((VLException) e).getFeil().getKode()
                : null;
    }

    public String writeValueAsString() throws IOException {
        return objectWriter.writeValueAsString(this);
    }

    public void setBlokkerendeProsessTaskId(Long blokkerendeProsessTaskId) {
        this.blokkerendeProsessTaskId = blokkerendeProsessTaskId;
    }

}

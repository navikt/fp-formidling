package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.net.URI;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Asynk status returnert fra server ved long-polling jobs. Typisk flyt:
 * 1. Klient sender request, får HTTP 202 Accepted + Location (for status url)
 * 2. status url gir HTTP 200 + denne som output sålenge jobb ikke er ferdig
 * 3. status url gir HTTP 303 + Location for endelig svar når jobb er fedig.
 */
@JsonInclude(Include.NON_NULL)
public class AsyncPollingStatus {

    private Status status;
    private LocalDateTime eta;
    private String message;
    private Long pollIntervalMillis;
    private URI location;
    private URI cancelUri;
    private boolean readOnly;

    AsyncPollingStatus() {
    }

    public AsyncPollingStatus(Status status) {
        this(status, null);
    }

    public AsyncPollingStatus(Status status, String message) {
        this(status, message, 0L);
    }

    public AsyncPollingStatus(Status status, String message, long pollIntervalMillis) {
        this(status, null, message, null, pollIntervalMillis);
    }

    public AsyncPollingStatus(Status status, LocalDateTime eta, String message) {
        this(status, eta, message, null, null);
    }

    public AsyncPollingStatus(Status status, LocalDateTime eta, String message, URI cancelUri, Long pollIntervalMillis) {
        this.status = status;
        this.eta = eta;
        this.message = message;
        this.cancelUri = cancelUri;
        this.pollIntervalMillis = pollIntervalMillis;
        this.readOnly = status == Status.PENDING || status == Status.DELAYED || status == Status.HALTED;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getEta() {
        return eta;
    }

    public String getMessage() {
        return message;
    }

    public URI getCancelUri() {
        return cancelUri;
    }

    public Long getPollIntervalMillis() {
        return pollIntervalMillis;
    }

    public URI getLocation() {
        // kan returneres også i tilfelle feil, for å kunne hente nåværende tilstand, uten hensyn til hva som ikke kan kjøres videre.
        return location;
    }

    public void setLocation(URI uri) {
        this.location = uri;
    }

    public boolean isPending() {
        return Status.PENDING.equals(getStatus());
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public enum Status {
        PENDING(200),
        COMPLETE(303),
        DELAYED(418),
        CANCELLED(418),
        HALTED(418);

        private int httpStatus;

        Status(int httpStatus){
            this.httpStatus = httpStatus;
        }

        public int getHttpStatus() {
            return httpStatus;
        }
    }
}

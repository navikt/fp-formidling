package no.nav.foreldrepenger.fpformidling.integrasjon.http;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import javax.ws.rs.core.MediaType;

public interface RequestKonfig {

    String getCallId();

    default String getAccept() { return APPLICATION_JSON.getMimeType(); }

    default String getContentType() { return MediaType.APPLICATION_JSON; }

    default int getTimeout() { return 5; }

}

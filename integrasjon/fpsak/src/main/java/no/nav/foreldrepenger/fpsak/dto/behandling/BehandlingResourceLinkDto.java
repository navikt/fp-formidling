package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingResourceLinkDto {
    private String href;
    private String rel;
    private String type;
    private BehandlingRelLinkPayloadDto requestPayload;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BehandlingRelLinkPayloadDto getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(BehandlingRelLinkPayloadDto requestPayload) {
        this.requestPayload = requestPayload;
    }

    @Override
    public String toString() {
        return "BehandlingRelLinksDto{" +
                "href='" + href + '\'' +
                ", rel='" + rel + '\'' +
                ", type='" + type + '\'' +
                ", requestPayload=" + (requestPayload != null ? requestPayload.toString() : null) +
                '}';
    }
}

package no.nav.foreldrepenger.fpformidling.behandling;

public class BehandlingResourceLink {
    private String href;
    private String rel;
    private String type;
    private BehandlingRelLinkPayload requestPayload;

    private BehandlingResourceLink(Builder builder) {
        href = builder.href;
        rel = builder.rel;
        type = builder.type;
        setRequestPayload(builder.requestPayload);
    }

    public static Builder ny() {
        return new Builder();
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public String getType() {
        return type;
    }

    public BehandlingRelLinkPayload getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(BehandlingRelLinkPayload requestPayload) {
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

    public static final class Builder {
        private String href;
        private String rel;
        private String type;
        private BehandlingRelLinkPayload requestPayload;

        private Builder() {
        }

        public Builder medHref(String href) {
            this.href = href;
            return this;
        }

        public Builder medRel(String rel) {
            this.rel = rel;
            return this;
        }

        public Builder medType(String type) {
            this.type = type;
            return this;
        }

        public Builder medRequestPayload(BehandlingRelLinkPayload requestPayload) {
            this.requestPayload = requestPayload;
            return this;
        }

        public BehandlingResourceLink build() {
            return new BehandlingResourceLink(this);
        }
    }
}

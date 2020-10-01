package no.nav.foreldrepenger.melding.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Sak {
    @JsonProperty("fagsakId")
    private String fagsakId;
    @JsonProperty("fagsaksystem")
    private String fagsaksystem;
    @JsonProperty("sakstype")
    private String sakstype;

    @JsonCreator
    public Sak(@JsonProperty("fagsakId") String fagsakId,
               @JsonProperty("fagsaksystem") String fagsaksystem,
               @JsonProperty("sakstype") String sakstype) {
        this.sakstype = sakstype;
        this.fagsakId = fagsakId;
        this.fagsaksystem = fagsaksystem;
    }

    public String getFagsakId() {
        return fagsakId;
    }

    public String getFagsaksystem() {
        return fagsaksystem;
    }

    public String getSakstype() {
        return sakstype;
    }

    @Override
    public String toString() {
        return "Sak{" +
                "fagsakId='" + fagsakId + '\'' +
                ", fagsaksystem='" + fagsaksystem + '\'' +
                ", sakstype='" + sakstype + '\'' +
                '}';
        }
    }

package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Sak {

    @JsonProperty("fagsakId")
    String fagsakId;
    @JsonProperty("fagsaksystem")
    String fagsaksystem;
    @JsonProperty("sakstype")
    private String sakstype;
    @JsonProperty("arkivsaksnummer")
    private String arkivsaksnummer;
    @JsonProperty("arkivsaksystem")
    private String arkivsaksystem;

    @JsonCreator
    public Sak(@JsonProperty("fagsakId") String fagsakId,
               @JsonProperty("fagsaksystem") String fagsaksystem,
               @JsonProperty("sakstype") String sakstype,
               @JsonProperty("arkivsaksnummer") String arkivsaksnummer,
               @JsonProperty("arkivsaksystem") String arkivsaksystem) {
        this.fagsakId = fagsakId;
        this.fagsaksystem = fagsaksystem;
        this.sakstype = sakstype;
        this.arkivsaksnummer = arkivsaksnummer;
        this.arkivsaksystem = arkivsaksystem;
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

    public String getArkivsaksnummer() {
        return arkivsaksnummer;
    }

    public String getArkivsaksystem() {
        return arkivsaksystem;
    }

    @Override
    public String toString() {
        return "Sak{" +
                "sakstype='" + sakstype + '\'' +
                ", arkivsaksnummer='" + arkivsaksnummer + '\'' +
                ", arkivsaksystem='" + arkivsaksystem + '\'' +
                '}';
        }
    }

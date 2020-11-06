package no.nav.foreldrepenger.melding.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Sak {
    @JsonProperty("sakstype")
    private String sakstype;
    @JsonProperty("arkivsaksnummer")
    private String arkivsaksnummer;
    @JsonProperty("arkivsaksystem")
    private String arkivsaksystem;

    @JsonCreator
    public Sak(@JsonProperty("sakstype") String sakstype,
               @JsonProperty("arkivsaksnummer") String arkivsaksnummer,
               @JsonProperty("arkivsaksystem") String arkivsaksystem) {
        this.sakstype = sakstype;
        this.arkivsaksnummer = arkivsaksnummer;
        this.arkivsaksystem = arkivsaksystem;
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
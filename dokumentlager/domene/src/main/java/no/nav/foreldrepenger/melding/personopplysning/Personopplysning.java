package no.nav.foreldrepenger.melding.personopplysning;

public class Personopplysning {
    private String fnr;
    private Long aktoerId;
    private String navn;
    private Boolean harVerge;

    public Personopplysning(String fnr, Long aktoerId, String navn, Boolean harVerge) {
        this.fnr = fnr;
        this.aktoerId = aktoerId;
        this.navn = navn;
        this.harVerge = harVerge;
    }

    public String getFnr() {
        return fnr;
    }

    public Long getAktoerId() {
        return aktoerId;
    }

    public String getNavn() {
        return navn;
    }

    public Boolean getHarVerge() {
        return harVerge;
    }
}

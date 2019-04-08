package no.nav.foreldrepenger.melding.fagsak;

public class Fagsak {
    private Long id;
    private String brukerRolle; //Kode
    private String ytelseType; //Kode
    private String saksnummer;

    private Object navBruker;

    public Long getId() {
        return id;
    }

    public String getBrukerRolle() {
        return brukerRolle;
    }

    public String getYtelseType() {
        return ytelseType;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public Object getNavBruker() {
        return navBruker;
    }

    public String getRelasjonsRolleType() {
        return brukerRolle;
    }
}

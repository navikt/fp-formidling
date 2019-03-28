package no.nav.foreldrepenger.melding.familiehendelse;

public class FamilieHendelse {
    private String familieHendelseType; //Kodeliste.FamilieHendelseType
    private int antallBarn;
    private String barna;
    private String UidentifisertBarn;
    private String terminbekreftelse;

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public String getBarna() {
        return barna;
    }

    public String getUidentifisertBarn() {
        return UidentifisertBarn;
    }

    public String getTerminbekreftelse() {
        return terminbekreftelse;
    }
}

package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

public class Stønadskonto {
    private String stønadskontoType; //Kode Stønadskontoberegning.Kodeliste.StønadskontoType
    private int maxDager;

    public String getStønadskontoType() {
        return stønadskontoType;
    }

    public int getMaxDager() {
        return maxDager;
    }
}

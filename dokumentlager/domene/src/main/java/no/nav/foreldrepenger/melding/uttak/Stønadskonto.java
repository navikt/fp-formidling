package no.nav.foreldrepenger.melding.uttak;

public class Stønadskonto {
    private Integer maxDager;
    private StønadskontoType stønadskontoType;
    private Stønadskontoberegning stønadskontoberegning;

    public Integer getMaxDager() {
        return maxDager;
    }

    public StønadskontoType getStønadskontoType() {
        return stønadskontoType;
    }
}

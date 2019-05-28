package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

public class UttakDto {
    private String stonadskontoType;
    private String periodeResultatType;
    private boolean gradering;

    public String getStonadskontoType() {
        return stonadskontoType;
    }

    public String getPeriodeResultatType() {
        return periodeResultatType;
    }

    public boolean isGradering() {
        return gradering;
    }

    public void setStonadskontoType(String stonadskontoType) {
        this.stonadskontoType = stonadskontoType;
    }

    public void setPeriodeResultatType(String periodeResultatType) {
        this.periodeResultatType = periodeResultatType;
    }

    public void setGradering(boolean gradering) {
        this.gradering = gradering;
    }
}

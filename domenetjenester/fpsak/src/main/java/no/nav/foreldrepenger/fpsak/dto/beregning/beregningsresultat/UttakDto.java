package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

public class UttakDto {
    private int trekkdager;
    private String stonadskontoType;
    private String periodeResultatType;
    private boolean gradering;

    public int getTrekkdager() {
        return trekkdager;
    }

    public String getStonadskontoType() {
        return stonadskontoType;
    }

    public String getPeriodeResultatType() {
        return periodeResultatType;
    }

    public boolean isGradering() {
        return gradering;
    }

    public void setTrekkdager(int trekkdager) {
        this.trekkdager = trekkdager;
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

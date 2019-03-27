package no.nav.foreldrepenger.fpsak.dto.soknad;

public class OppgittRettighetDto {
    private boolean omsorgForBarnet;
    private boolean aleneomsorgForBarnet;

    public OppgittRettighetDto() {
        // trengs for deserialisering av JSON
    }

    public boolean isOmsorgForBarnet() {
        return omsorgForBarnet;
    }

    public void setOmsorgForBarnet(boolean omsorgForBarnet) {
        this.omsorgForBarnet = omsorgForBarnet;
    }

    public boolean isAleneomsorgForBarnet() {
        return aleneomsorgForBarnet;
    }

    public void setAleneomsorgForBarnet(boolean aleneomsorgForBarnet) {
        this.aleneomsorgForBarnet = aleneomsorgForBarnet;
    }
}

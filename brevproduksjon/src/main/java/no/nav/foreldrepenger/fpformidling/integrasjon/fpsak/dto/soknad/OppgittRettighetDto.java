package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.soknad;

public class OppgittRettighetDto {
    private boolean aleneomsorgForBarnet;

    public OppgittRettighetDto() {
        // trengs for deserialisering av JSON
    }

    public boolean isAleneomsorgForBarnet() {
        return aleneomsorgForBarnet;
    }

    public void setAleneomsorgForBarnet(boolean aleneomsorgForBarnet) {
        this.aleneomsorgForBarnet = aleneomsorgForBarnet;
    }
}

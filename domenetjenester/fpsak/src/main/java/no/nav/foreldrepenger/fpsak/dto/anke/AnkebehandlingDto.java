package no.nav.foreldrepenger.fpsak.dto.anke;


public class AnkebehandlingDto {

    private AnkeVurderingResultatDto ankeVurderingResultat;


    public AnkebehandlingDto() {
        // trengs for deserialisering av JSON
    }

    public AnkeVurderingResultatDto getAnkeVurderingResultat() {
        return ankeVurderingResultat;
    }

    void setAnkeVurderingResultat(AnkeVurderingResultatDto ankeVurderingResultat) {
        this.ankeVurderingResultat = ankeVurderingResultat;
    }
}

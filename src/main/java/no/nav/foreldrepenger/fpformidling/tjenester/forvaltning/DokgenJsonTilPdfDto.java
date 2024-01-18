package no.nav.foreldrepenger.fpformidling.tjenester.forvaltning;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.QueryParam;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

class DokgenJsonTilPdfDto implements AbacDto {

    @NotNull
    @QueryParam("malType")
    private String malType;

    @NotNull
    @QueryParam("språkKode")
    private String språkKode;

    @NotNull
    @QueryParam("dokumentdataKlasse")
    private String dokumentdataKlasse;

    @NotNull
    @QueryParam("dokumentdataJson")
    private String dokumentdataJson;

    DokgenJsonTilPdfDto(@NotNull String malType, @NotNull String språkKode, @NotNull String dokumentdataKlasse, @NotNull String dokumentdataJson) {
        this.malType = malType;
        this.språkKode = språkKode;
        this.dokumentdataKlasse = dokumentdataKlasse;
        this.dokumentdataJson = dokumentdataJson;
    }

    public DokgenJsonTilPdfDto() {
    }

    public String getMalType() {
        return malType;
    }

    public String getSpråkKode() {
        return språkKode;
    }

    public String getDokumentdataKlasse() {
        return dokumentdataKlasse;
    }

    public String getDokumentdataJson() {
        return dokumentdataJson;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}

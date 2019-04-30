package no.nav.foreldrepenger.melding.brevbestiller.dto;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalRestriksjon;

public class BrevmalDto {
    private String kode;
    private String navn;
    private DokumentMalRestriksjon restriksjon;
    private boolean tilgjengelig;

    public BrevmalDto(String kode, String navn, DokumentMalRestriksjon restriksjon, boolean tilgjengelig) {
        this.kode = kode;
        this.navn = navn;
        this.restriksjon = restriksjon;
        this.tilgjengelig = tilgjengelig;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public DokumentMalRestriksjon getRestriksjon() {
        return restriksjon;
    }

    public void setRestriksjon(DokumentMalRestriksjon restriksjon) {
        this.restriksjon = restriksjon;
    }

    public boolean getTilgjengelig() {
        return tilgjengelig;
    }

    public void setTilgjengelig(boolean tilgjengelig) {
        this.tilgjengelig = tilgjengelig;
    }

}

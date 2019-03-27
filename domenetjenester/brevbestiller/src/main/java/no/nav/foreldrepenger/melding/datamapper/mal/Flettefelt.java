package no.nav.foreldrepenger.melding.datamapper.mal;

import no.nav.foreldrepenger.melding.datamapper.util.FlettefeltJsonObjectMapper;

public class Flettefelt {
    private String feltnavn;
    private String feltverdi;
    private boolean strukturert;

    public void setFeltnavn(String feltnavn) {
        this.feltnavn = feltnavn;
    }

    public void setFeltverdi(String feltverdi) {
        this.feltverdi = feltverdi;
        this.strukturert = false;
    }

    public String getFeltnavn() {
        return feltnavn;
    }

    public String getFeltverdi() {
        return feltverdi;
    }

    public <T> T getStrukturertVerdi(Class<T> targetClass) {
        return FlettefeltJsonObjectMapper.readValue(feltverdi, targetClass);
    }

    public void setStukturertVerdi(Object stukturertVerdi) {
        this.feltverdi = toJson(stukturertVerdi);
        this.strukturert = true;
    }

    private String toJson(Object stukturertVerdi) {
        return FlettefeltJsonObjectMapper.toJson(stukturertVerdi);
    }

    public boolean isStrukturert() {
        return strukturert;
    }
}

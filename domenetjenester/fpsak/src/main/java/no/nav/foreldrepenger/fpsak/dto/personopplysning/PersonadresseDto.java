package no.nav.foreldrepenger.fpsak.dto.personopplysning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonadresseDto {
    private KodeDto adresseType;
    private String mottakerNavn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String postNummer;
    private String poststed;
    private String land;

    public KodeDto getAdresseType() {
        return adresseType;
    }

    public void setAdresseType(KodeDto adresseType) {
        this.adresseType = adresseType;
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public void setMottakerNavn(String mottakerNavn) {
        this.mottakerNavn = mottakerNavn;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    public void setAdresselinje1(String adresselinje1) {
        this.adresselinje1 = adresselinje1;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    public void setAdresselinje2(String adresselinje2) {
        this.adresselinje2 = adresselinje2;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    public void setAdresselinje3(String adresselinje3) {
        this.adresselinje3 = adresselinje3;
    }

    public String getPostNummer() {
        return postNummer;
    }

    public void setPostNummer(String postNummer) {
        this.postNummer = postNummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    @Override
    public String toString() {
        return "PersonadresseDto{" +
                "adresseType=" + adresseType +
                ", mottakerNavn='" + mottakerNavn + '\'' +
                ", adresselinje1='" + adresselinje1 + '\'' +
                ", adresselinje2='" + adresselinje2 + '\'' +
                ", adresselinje3='" + adresselinje3 + '\'' +
                ", postNummer='" + postNummer + '\'' +
                ", poststed='" + poststed + '\'' +
                ", land='" + land + '\'' +
                '}';
    }
}

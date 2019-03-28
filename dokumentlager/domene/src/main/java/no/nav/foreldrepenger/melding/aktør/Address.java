package no.nav.foreldrepenger.melding.aktør;

import java.util.Objects;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonadresseDto;

public class Address {
    private String mottakerNavn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String postnummer;
    private String poststed;
    private String land;

    public Address(PersonadresseDto dto) {
        Objects.requireNonNull(dto, "PersonadresseDto"); //$NON-NLS-1$
        this.mottakerNavn = dto.getMottakerNavn();
        this.adresselinje1 = dto.getAdresselinje1();
        this.adresselinje2 = dto.getAdresselinje2();
        this.adresselinje3 = dto.getAdresselinje3();
        this.postnummer = dto.getPostNummer();
        this.poststed = dto.getPoststed();
        this.land = dto.getLand();
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public String getLand() {
        return land;
    }
}

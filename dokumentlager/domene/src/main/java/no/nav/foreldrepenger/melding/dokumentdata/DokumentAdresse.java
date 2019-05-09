package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Objects;

public class DokumentAdresse {

    private String mottakerNavn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String postnummer;
    private String poststed;
    private String land;

    private DokumentAdresse() {
    }

    DokumentAdresse(DokumentAdresse adresse) {
        Objects.requireNonNull(adresse, "adresse"); //$NON-NLS-1$
        this.mottakerNavn = adresse.mottakerNavn;
        this.adresselinje1 = adresse.adresselinje1;
        this.adresselinje2 = adresse.adresselinje2;
        this.adresselinje3 = adresse.adresselinje3;
        this.postnummer = adresse.postnummer;
        this.poststed = adresse.poststed;
        this.land = adresse.land;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DokumentAdresse)) {
            return false;
        }
        DokumentAdresse other = (DokumentAdresse) o;
        return Objects.equals(this.mottakerNavn, other.mottakerNavn)
                && Objects.equals(this.adresselinje1, other.adresselinje1)
                && Objects.equals(this.adresselinje2, other.adresselinje2)
                && Objects.equals(this.adresselinje3, other.adresselinje3)
                && Objects.equals(this.postnummer, other.postnummer)
                && Objects.equals(this.poststed, other.poststed)
                && Objects.equals(this.land, other.land);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mottakerNavn, adresselinje1, adresselinje2, adresselinje3, postnummer, poststed, land);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<mottakerNavn=" + mottakerNavn + ", adresselinje1=" + adresselinje1 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + ", poststed=" + poststed + ", land=" + land + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static class Builder {
        private DokumentAdresse adresseMal;

        public Builder(DokumentAdresse adresse) {
            if (adresse != null) {
                adresseMal = new DokumentAdresse(adresse);
            } else {
                adresseMal = new DokumentAdresse();
            }
        }

        public Builder() {
            this.adresseMal = new DokumentAdresse();
        }

        public Builder medMottakerNavn(String mottakerNavn) {
            adresseMal.mottakerNavn = mottakerNavn;
            return this;

        }

        public Builder medAdresselinje1(String adresselinje1) {
            adresseMal.adresselinje1 = adresselinje1;
            return this;
        }

        public Builder medAdresselinje2(String adresselinje2) {
            adresseMal.adresselinje2 = adresselinje2;
            return this;
        }

        public Builder medAdresselinje3(String adresselinje3) {
            adresseMal.adresselinje3 = adresselinje3;
            return this;
        }

        public Builder medPostNummer(String postNummer) {
            adresseMal.postnummer = postNummer;
            return this;

        }

        public Builder medPoststed(String poststed) {
            adresseMal.poststed = poststed;
            return this;

        }

        public Builder medLand(String land) {
            adresseMal.land = land;
            return this;

        }

        public DokumentAdresse build() {
            return adresseMal;
        }
    }
}

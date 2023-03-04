package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FellesDokumentdata {
    private String søkerNavn;
    private String søkerPersonnummer;
    private FritekstDto fritekst;
    private String brevDato;
    private boolean erAutomatiskBehandlet;
    private boolean erKopi;
    private boolean harVerge;
    private String saksnummer;
    private String mottakerNavn;
    private String ytelseType;
    private boolean behandlesAvKA;
    private boolean erUtkast;

    public String getSøkerNavn() {
        return søkerNavn;
    }

    public String getSøkerPersonnummer() {
        return søkerPersonnummer;
    }

    public FritekstDto getFritekst() {
        return fritekst;
    }

    public String getBrevDato() {
        return brevDato;
    }

    public boolean getErAutomatiskBehandlet() {
        return erAutomatiskBehandlet;
    }

    public boolean getErKopi() {
        return erKopi;
    }

    public boolean getHarVerge() {
        return harVerge;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public String getYtelseType() {
        return ytelseType;
    }

    public boolean getBehandlesAvKA() {
        return behandlesAvKA;
    }

    public boolean getErUtkast() {
        return erUtkast;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (FellesDokumentdata) object;
        return Objects.equals(søkerNavn, that.søkerNavn) && Objects.equals(søkerPersonnummer, that.søkerPersonnummer) && Objects.equals(fritekst,
            that.fritekst) && Objects.equals(brevDato, that.brevDato) && Objects.equals(erAutomatiskBehandlet, that.erAutomatiskBehandlet)
            && Objects.equals(erKopi, that.erKopi) && Objects.equals(harVerge, that.harVerge) && Objects.equals(saksnummer, that.saksnummer)
            && Objects.equals(mottakerNavn, that.mottakerNavn) && Objects.equals(ytelseType, that.ytelseType) && Objects.equals(behandlesAvKA,
            that.behandlesAvKA) && Objects.equals(erUtkast, that.erUtkast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(søkerNavn, søkerPersonnummer, fritekst, brevDato, erAutomatiskBehandlet, erKopi, harVerge, saksnummer, mottakerNavn,
            ytelseType, behandlesAvKA, erUtkast);
    }

    // Til bruk når alternativt ulansert brev skal genereres i testfasen av innvilgelse FP
    public void anonymiser() {
        this.søkerNavn = søkerNavn.substring(0, 3) + " ANONYMISERT";
        this.søkerPersonnummer = søkerPersonnummer.substring(0, 4) + "** *****";
        if (this.mottakerNavn != null) {
            this.mottakerNavn = mottakerNavn.substring(0, 3) + " ANONYMISERT";
        }
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private FellesDokumentdata kladd;

        private Builder() {
            this.kladd = new FellesDokumentdata();
        }

        public Builder medSøkerNavn(String søkerNavn) {
            this.kladd.søkerNavn = søkerNavn;
            return this;
        }

        public Builder medSøkerPersonnummer(String søkerPersonnummer) {
            this.kladd.søkerPersonnummer = søkerPersonnummer;
            return this;
        }

        public Builder medFritekst(FritekstDto fritekst) {
            this.kladd.fritekst = fritekst;
            return this;
        }

        public Builder medBrevDato(String brevDato) {
            this.kladd.brevDato = brevDato;
            return this;
        }

        public Builder medErAutomatiskBehandlet(boolean erAutomatiskBehandlet) {
            this.kladd.erAutomatiskBehandlet = erAutomatiskBehandlet;
            return this;
        }

        public Builder medErKopi(boolean erKopi) {
            this.kladd.erKopi = erKopi;
            return this;
        }

        public Builder medHarVerge(boolean harVerge) {
            this.kladd.harVerge = harVerge;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            this.kladd.saksnummer = saksnummer;
            return this;
        }

        public Builder medMottakerNavn(String mottakerNavn) {
            this.kladd.mottakerNavn = mottakerNavn;
            return this;
        }

        public Builder medYtelseType(String ytelseType) {
            this.kladd.ytelseType = ytelseType;
            return this;
        }

        public Builder medBehandlesAvKA(boolean behandlesAvKA) {
            this.kladd.behandlesAvKA = behandlesAvKA;
            return this;
        }

        public Builder medErUtkast(boolean erUtkast) {
            this.kladd.erUtkast = erUtkast;
            return this;
        }

        public FellesDokumentdata build() {
            return this.kladd;
        }
    }
}

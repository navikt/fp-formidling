package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeldrepengerAvslagDokumentdata extends Dokumentdata {
    private String relasjonskode;
    private String mottattDato;
    private boolean gjelderFødsel;
    private boolean barnErFødt;
    private boolean annenForelderHarRett;
    private int antallBarn;
    private long halvG;
    private int klageFristUker;
    private String lovhjemmelForAvslag;
    private int antallPerioder;
    private List<AvslåttPeriode> avslåttePerioder;

    public String getRelasjonskode() {
        return relasjonskode;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public boolean getGjelderFødsel() {
        return gjelderFødsel;
    }

    public boolean getBarnErFødt() {
        return barnErFødt;
    }

    public boolean getAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public long getHalvG() {
        return halvG;
    }

    public int getKlageFristUker() {
        return klageFristUker;
    }

    public String getLovhjemmelForAvslag() {
        return lovhjemmelForAvslag;
    }

    public int getAntallPerioder() {
        return antallPerioder;
    }

    public List<AvslåttPeriode> getAvslåttePerioder() {
        return avslåttePerioder;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (ForeldrepengerAvslagDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(relasjonskode, that.relasjonskode)
                && Objects.equals(mottattDato, that.mottattDato)
                && Objects.equals(gjelderFødsel, that.gjelderFødsel)
                && Objects.equals(barnErFødt, that.barnErFødt)
                && Objects.equals(annenForelderHarRett, that.annenForelderHarRett)
                && Objects.equals(antallBarn, that.antallBarn)
                && Objects.equals(halvG, that.halvG)
                && Objects.equals(klageFristUker, that.klageFristUker)
                && Objects.equals(lovhjemmelForAvslag, that.lovhjemmelForAvslag)
                && Objects.equals(antallPerioder, that.antallPerioder)
                && Objects.equals(avslåttePerioder, that.avslåttePerioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, relasjonskode, mottattDato, gjelderFødsel, barnErFødt, annenForelderHarRett,
                antallBarn, halvG, klageFristUker, lovhjemmelForAvslag, antallPerioder, avslåttePerioder);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private ForeldrepengerAvslagDokumentdata kladd;

        private Builder() {
            this.kladd = new ForeldrepengerAvslagDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medRelasjonskode(String relasjonskode) {
            this.kladd.relasjonskode = relasjonskode;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public Builder medGjelderFødsel(boolean gjelderFødsel) {
            this.kladd.gjelderFødsel = gjelderFødsel;
            return this;
        }

        public Builder medBarnErFødt(boolean barnErFødt) {
            this.kladd.barnErFødt = barnErFødt;
            return this;
        }

        public Builder medAnnenForelderHarRett(boolean annenForelderHarRett) {
            this.kladd.annenForelderHarRett = annenForelderHarRett;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public Builder medHalvG(long halvG) {
            this.kladd.halvG = halvG;
            return this;
        }

        public Builder medKlageFristUker(int klageFristUker) {
            this.kladd.klageFristUker = klageFristUker;
            return this;
        }

        public Builder medLovhjemmelForAvslag(String lovhjemmelForAvslag) {
            this.kladd.lovhjemmelForAvslag = lovhjemmelForAvslag;
            return this;
        }

        public Builder medAntallPerioder(int antallPerioder) {
            this.kladd.antallPerioder = antallPerioder;
            return this;
        }

        public Builder medAvslåttePerioder(List<AvslåttPeriode> avslåttePerioder) {
            this.kladd.avslåttePerioder = avslåttePerioder;
            return this;
        }

        public ForeldrepengerAvslagDokumentdata build() {
            return this.kladd;
        }
    }
}

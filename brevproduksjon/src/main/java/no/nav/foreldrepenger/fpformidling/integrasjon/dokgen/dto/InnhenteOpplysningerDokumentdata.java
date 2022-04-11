package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InnhenteOpplysningerDokumentdata extends Dokumentdata {
    private boolean førstegangsbehandling;
    private boolean revurdering;
    private boolean endringssøknad;
    private boolean død;
    private boolean klage;
    private String søknadDato;
    private String fristDato;

    public boolean getFørstegangsbehandling() {
        return førstegangsbehandling;
    }

    public boolean getRevurdering() {
        return revurdering;
    }

    public boolean getEndringssøknad() {
        return endringssøknad;
    }

    public boolean getDød() {
        return død;
    }

    public boolean getKlage() {
        return klage;
    }

    public String getSøknadDato() {
        return søknadDato;
    }

    public String getFristDato() {
        return fristDato;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (InnhenteOpplysningerDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(førstegangsbehandling, that.førstegangsbehandling)
                && Objects.equals(revurdering, that.revurdering)
                && Objects.equals(endringssøknad, that.endringssøknad)
                && Objects.equals(død, that.død)
                && Objects.equals(klage, that.klage)
                && Objects.equals(søknadDato, that.søknadDato)
                && Objects.equals(fristDato, that.fristDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, førstegangsbehandling, revurdering, endringssøknad, død, klage,
                søknadDato, fristDato);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private InnhenteOpplysningerDokumentdata kladd;

        private Builder() {
            this.kladd = new InnhenteOpplysningerDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medFørstegangsbehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public Builder medRevurdering(boolean revurdering) {
            this.kladd.revurdering= revurdering;
            return this;
        }

        public Builder medEndringssøknad(boolean endringssøknad) {
            this.kladd.endringssøknad = endringssøknad;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medKlage(boolean klage) {
            this.kladd.klage = klage;
            return this;
        }

        public Builder medSøknadDato(String søknadsdato) {
            this.kladd.søknadDato = søknadsdato;
            return this;
        }

        public Builder medFristDato(String fristDato) {
            this.kladd.fristDato = fristDato;
            return this;
        }

        public InnhenteOpplysningerDokumentdata build() {
            return this.kladd;
        }
    }
}

package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EngangsstønadInnvilgelseDokumentdata extends Dokumentdata {
    private boolean revurdering;
    private boolean førstegangsbehandling;
    private boolean medhold;
    private String innvilgetBeløp;
    private int klagefristUker;
    private boolean død;
    private boolean fbEllerMedhold;
    private boolean erEndretSats;

    public boolean getRevurdering() {
        return revurdering;
    }

    public boolean getMedhold() {
        return medhold;
    }

    public String getInnvilgetBeløp() {
        return innvilgetBeløp;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public boolean getDød() {
        return død;
    }

    public boolean getFbEllerMedhold() {
        return fbEllerMedhold;
    }

    public boolean getErEndretSats() {
        return erEndretSats;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (EngangsstønadInnvilgelseDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(revurdering, that.revurdering) && Objects.equals(førstegangsbehandling,
            that.førstegangsbehandling) && Objects.equals(medhold, that.medhold) && Objects.equals(innvilgetBeløp, that.innvilgetBeløp)
            && Objects.equals(klagefristUker, that.klagefristUker) && Objects.equals(død, that.død) && Objects.equals(fbEllerMedhold,
            that.fbEllerMedhold) && Objects.equals(erEndretSats, that.erEndretSats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, revurdering, førstegangsbehandling, medhold, innvilgetBeløp, klagefristUker, død, fbEllerMedhold, erEndretSats);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private EngangsstønadInnvilgelseDokumentdata kladd;

        private Builder() {
            this.kladd = new EngangsstønadInnvilgelseDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medRevurdering(boolean revurdering) {
            this.kladd.revurdering = revurdering;
            return this;
        }

        public Builder medFørstegangsbehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public Builder medMedhold(boolean medhold) {
            this.kladd.medhold = medhold;
            return this;
        }

        public Builder medInnvilgetBeløp(String innvilgetBeløp) {
            this.kladd.innvilgetBeløp = innvilgetBeløp;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medFbEllerMedhold(boolean fbEllerMedhold) {
            this.kladd.fbEllerMedhold = fbEllerMedhold;
            return this;
        }

        public Builder medErEndretSats(boolean erEndretSats) {
            this.kladd.erEndretSats = erEndretSats;
            return this;
        }

        public EngangsstønadInnvilgelseDokumentdata build() {
            return this.kladd;
        }
    }
}

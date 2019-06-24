package no.nav.foreldrepenger.melding.uttak.svp;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class SvpUttaksresultat {

    private List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold;
    private List<SvpUttakResultatPerioder> uttakPerioder;

    private List<Map> avslagPerioder;

    public List<SvpUttakResultatPerioder> getUttakPerioder() {
        return uttakPerioder;
    }

    public List<Map> getAvslagPerioder() {
        return avslagPerioder;
    }

    private SvpUttaksresultat(Builder builder) {
        uttakResultatArbeidsforhold = builder.uttakResultatArbeidsforhold;
        uttakPerioder = builder.uttakResultatPerioder;
        avslagPerioder = builder.avslåttePerioder;
    }

    public static Builder ny() {
        return new Builder();
    }

    public List<SvpUttakResultatArbeidsforhold> getUttakResultatArbeidsforhold() {
        return uttakResultatArbeidsforhold.stream().sorted(Comparator.comparing(getSammenligningsDato()))
                .collect(Collectors.toList());
    }

    private Function<SvpUttakResultatArbeidsforhold, DatoIntervall> getSammenligningsDato() {
        return o -> !o.getPerioder().isEmpty() ?
                o.getPerioder().get(0).getTidsperiode() : DatoIntervall.fraOgMed(TIDENES_ENDE);
    }

    public static final class Builder {
        private List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold = new ArrayList<>();
        private List<SvpUttakResultatPerioder> uttakResultatPerioder = new ArrayList();
        private List<Map> avslåttePerioder = new ArrayList();

        public Builder() {
        }

        public Builder medUttakResultatArbeidsforhold(SvpUttakResultatArbeidsforhold uttakResultatArbeidsforhold) {
            this.uttakResultatArbeidsforhold.add(uttakResultatArbeidsforhold);
            return this;
        }

        public SvpUttaksresultat build() {
            return new SvpUttaksresultat(this);
        }

        public Builder medUttakResultatPerioder(SvpUttakResultatPerioder uttakResultatPerioder) {
            this.uttakResultatPerioder.add(uttakResultatPerioder);
            return this;
        }

        public Builder medAvslåttePerioder(List<Map> avslåttePerioder) {
            this.avslåttePerioder = avslåttePerioder;
            return this;
        }
    }
}

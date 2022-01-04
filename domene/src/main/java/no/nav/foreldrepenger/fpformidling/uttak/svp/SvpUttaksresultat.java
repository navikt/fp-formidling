package no.nav.foreldrepenger.fpformidling.uttak.svp;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

public class SvpUttaksresultat {

    private List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold;

    private SvpUttaksresultat(Builder builder) {
        uttakResultatArbeidsforhold = builder.uttakResultatArbeidsforhold;
    }

    public List<SvpUttakResultatArbeidsforhold> getUttakResultatArbeidsforhold() {
        return uttakResultatArbeidsforhold.stream()
                .sorted(Comparator.comparing(getSammenligningsDato()))
                .collect(Collectors.toList());
    }

    private Function<SvpUttakResultatArbeidsforhold, DatoIntervall> getSammenligningsDato() {
        return o -> !o.getPerioder().isEmpty()
                ? o.getPerioder().get(0).getTidsperiode()
                : DatoIntervall.fraOgMed(TIDENES_ENDE);
    }

    public static final class Builder {
        private List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold = new ArrayList<>();

        private Builder() {
            // Skjul default constructor
        }

        public static Builder ny() {
            return new Builder();
        }

        public Builder leggTilUttakResultatArbeidsforhold(SvpUttakResultatArbeidsforhold uttakResultatArbeidsforhold) {
            this.uttakResultatArbeidsforhold.add(uttakResultatArbeidsforhold);
            return this;
        }

        public SvpUttaksresultat build() {
            return new SvpUttaksresultat(this);
        }
    }
}

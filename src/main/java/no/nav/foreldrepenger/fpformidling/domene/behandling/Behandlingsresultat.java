package no.nav.foreldrepenger.fpformidling.domene.behandling;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;

public class Behandlingsresultat {
    private Avslagsårsak avslagsårsak; //Kode
    private String fritekstbrev;
    private String overskrift;
    private BehandlingResultatType behandlingResultatType;
    private String avslagarsakFritekst;
    private List<KonsekvensForYtelsen> konsekvenserForYtelsen; //Kode BehandlingsresultatKonsekvensForYtelsen
    private LocalDate skjæringstidspunkt;
    private boolean utenMinsterett;
    private boolean endretDekningsgrad;
    private LocalDate opphørsdato;

    //Objekter
    private String periode; //Uttaksperiodegrense
    private Behandling behandling;

    private Behandlingsresultat(Builder builder) {
        avslagsårsak = builder.avslagsårsak;
        fritekstbrev = builder.fritekstbrev;
        overskrift = builder.overskrift;
        behandlingResultatType = builder.behandlingResultatType;
        avslagarsakFritekst = builder.avslagarsakFritekst;
        konsekvenserForYtelsen = builder.konsekvenserForYtelsen;
        skjæringstidspunkt = builder.skjæringstidspunkt;
        utenMinsterett = builder.utenMinsterett;
        periode = builder.periode;
        behandling = builder.behandling;
        endretDekningsgrad = builder.endretDekningsgrad;
        opphørsdato = builder.opphørsdato;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean erInnvilget() {
        return BehandlingResultatType.INNVILGET.equals(behandlingResultatType);
    }

    public BehandlingResultatType getBehandlingResultatType() {
        return behandlingResultatType;
    }

    public Avslagsårsak getAvslagsårsak() {
        return avslagsårsak;
    }

    public String getFritekstbrev() {
        return fritekstbrev;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public String getAvslagarsakFritekst() {
        return avslagarsakFritekst;
    }

    public String getPeriode() {
        return periode;
    }

    public List<KonsekvensForYtelsen> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public Optional<LocalDate> getSkjæringstidspunkt() {
        return Optional.ofNullable(skjæringstidspunkt);
    }

    public boolean utenMinsterett() {
        return utenMinsterett;
    }

    public boolean erAvslått() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType);
    }

    public Behandling getBehandling() {
        return behandling;
    }

    public boolean isEndretDekningsgrad() {
        return endretDekningsgrad;
    }

    public Optional<LocalDate> getOpphørsdato() {
        return Optional.ofNullable(opphørsdato);
    }

    public static final class Builder {
        private Avslagsårsak avslagsårsak;
        private String fritekstbrev;
        private String overskrift;
        private BehandlingResultatType behandlingResultatType;
        private String avslagarsakFritekst;
        private List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>();
        private LocalDate skjæringstidspunkt;
        private boolean utenMinsterett = true;
        private String periode;
        private Behandling behandling;
        private boolean endretDekningsgrad;
        private LocalDate opphørsdato;

        private Builder() {
        }

        public Builder medAvslagsårsak(Avslagsårsak avslagsårsak) {
            this.avslagsårsak = avslagsårsak;
            return this;
        }

        public Builder medFritekstbrev(String fritekstbrev) {
            this.fritekstbrev = fritekstbrev;
            return this;
        }

        public Builder medOverskrift(String overskrift) {
            this.overskrift = overskrift;
            return this;
        }

        public Builder medBehandlingResultatType(BehandlingResultatType behandlingResultatType) {
            this.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public Builder medAvslagarsakFritekst(String avslagarsakFritekst) {
            this.avslagarsakFritekst = avslagarsakFritekst;
            return this;
        }

        public Builder medKonsekvenserForYtelsen(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
            this.konsekvenserForYtelsen = konsekvenserForYtelsen;
            return this;
        }

        public Builder medSkjæringstidspunkt(LocalDate skjæringstidspunkt) {
            this.skjæringstidspunkt = skjæringstidspunkt;
            return this;
        }

        public Builder medUtenMinsterett(boolean utenMinsterett) {
            this.utenMinsterett = utenMinsterett;
            return this;
        }

        public Builder medEndretDekningsgrad(boolean endretDekningsgrad) {
            this.endretDekningsgrad = endretDekningsgrad;
            return this;
        }

        public Builder medOpphørsdato(LocalDate opphørsdato) {
            this.opphørsdato = opphørsdato;
            return this;
        }

        public Builder periode(String periode) {
            this.periode = periode;
            return this;
        }

        public Builder behandling(Behandling behandling) {
            this.behandling = behandling;
            return this;
        }

        public Behandlingsresultat build() {
            return new Behandlingsresultat(this);
        }
    }
}

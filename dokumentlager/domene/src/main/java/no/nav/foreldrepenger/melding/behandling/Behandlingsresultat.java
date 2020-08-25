package no.nav.foreldrepenger.melding.behandling;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.melding.beregning.BeregningResultat;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.VilkårResultat;

public class Behandlingsresultat {
    private Avslagsårsak avslagsårsak; //Kode
    private String fritekstbrev;
    private String overskrift;
    private Vedtaksbrev vedtaksbrev;
    private BehandlingResultatType behandlingResultatType;
    private String avslagarsakFritekst;
    private List<KonsekvensForYtelsen> konsekvenserForYtelsen; //Kode BehandlingsresultatKonsekvensForYtelsen
    private Boolean erRevurderingMedUendretUtfall;

    //Objekter
    private String beregning; //BeregningResultat
    private VilkårResultat vilkårResultat; //VilkårResultat
    private String Periode; //Uttaksperiodegrense
    private BeregningResultat beregningResultat;
    private Behandling behandling;

    private Behandlingsresultat(Builder builder) {
        avslagsårsak = builder.avslagsårsak;
        fritekstbrev = builder.fritekstbrev;
        overskrift = builder.overskrift;
        vedtaksbrev = builder.vedtaksbrev;
        behandlingResultatType = builder.behandlingResultatType;
        avslagarsakFritekst = builder.avslagarsakFritekst;
        konsekvenserForYtelsen = builder.konsekvenserForYtelsen;
        erRevurderingMedUendretUtfall = builder.erRevurderingMedUendretUtfall;
        beregning = builder.beregning;
        vilkårResultat = builder.vilkårResultat;
        Periode = builder.Periode;
        beregningResultat = builder.beregningResultat;
        behandling = builder.behandling;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Vedtaksbrev getVedtaksbrev() {
        return vedtaksbrev;
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

    public String getBeregning() {
        return beregning;
    }

    public VilkårResultat getVilkårResultat() {
        return vilkårResultat;
    }

    public String getPeriode() {
        return Periode;
    }

    public List<KonsekvensForYtelsen> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public Boolean erRevurderingMedUendretUtfall() {
        return Boolean.TRUE.equals(erRevurderingMedUendretUtfall);
    }

    public boolean isBehandlingsresultatAvslåttOrOpphørt() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType)
                || BehandlingResultatType.OPPHØR.equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatAvslått() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType);
    }

    public boolean erEndretForeldrepenger() {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatOpphørt() {
        return BehandlingResultatType.OPPHØR.equals(behandlingResultatType);
    }

    public boolean erAvslått() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatInnvilget() {
        return BehandlingResultatType.INNVILGET.equals(behandlingResultatType);
    }

    public boolean erOpphørt() {
        return BehandlingResultatType.OPPHØR.equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatForeldrepengerEndret() {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingResultatType);
    }

    public BeregningResultat getBeregningResultat() {
        return beregningResultat;
    }

    public Behandling getBehandling() {
        return behandling;
    }

    public boolean isBehandlingHenlagt() {
        return BehandlingResultatType.getAlleHenleggelseskoder().contains(behandlingResultatType);
    }

    public static final class Builder {
        private Avslagsårsak avslagsårsak;
        private String fritekstbrev;
        private String overskrift;
        private Vedtaksbrev vedtaksbrev;
        private BehandlingResultatType behandlingResultatType;
        private String avslagarsakFritekst;
        private List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>();
        private Boolean erRevurderingMedUendretUtfall;
        private String beregning;
        private VilkårResultat vilkårResultat;
        private String Periode;
        private BeregningResultat beregningResultat;
        private Behandling behandling;

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

        public Builder medVedtaksbrev(Vedtaksbrev vedtaksbrev) {
            this.vedtaksbrev = vedtaksbrev;
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

        public Builder medErRevurderingMedUendretUtfall(Boolean erRevurderingMedUendretUtfall) {
            this.erRevurderingMedUendretUtfall = erRevurderingMedUendretUtfall;
            return this;
        }

        public Builder beregning(String beregning) {
            this.beregning = beregning;
            return this;
        }

        public Builder vilkårResultat(VilkårResultat vilkårResultat) {
            this.vilkårResultat = vilkårResultat;
            return this;
        }

        public Builder Periode(String Periode) {
            this.Periode = Periode;
            return this;
        }

        public Builder beregningResultat(BeregningResultat beregningResultat) {
            this.beregningResultat = beregningResultat;
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

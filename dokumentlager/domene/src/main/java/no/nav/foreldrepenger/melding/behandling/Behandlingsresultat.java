package no.nav.foreldrepenger.melding.behandling;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.beregning.BeregningResultat;
import no.nav.foreldrepenger.melding.vilkår.VilkårResultat;

public class Behandlingsresultat {
    private String avslagsårsak; //Kode
    private String fritekstbrev;
    private String overskrift;
    private String vedtaksbrev;
    private String behandlingResultatType;
    private String avslagarsakFritekst;
    private List<String> konsekvenserForYtelsen = new ArrayList<>(); //Kode BehandlingsresultatKonsekvensForYtelsen

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
        beregning = builder.beregning;
        vilkårResultat = builder.vilkårResultat;
        Periode = builder.Periode;
        beregningResultat = builder.beregningResultat;
        behandling = builder.behandling;
    }

    public static Behandlingsresultat fraDto(BehandlingsresultatDto dto) {
        Builder builder = ny();
        if (dto.getAvslagsarsak() != null) {
            builder.medAvslagsårsak(dto.getAvslagsarsak().kode);
        }
        if (dto.getType() != null) {
            builder.medBehandlingResultatType(dto.getType().kode);
        }
        builder.medFritekstbrev(dto.getFritekstbrev())
                .medOverskrift(dto.getOverskrift())
                .medVedtaksbrev(dto.getVedtaksbrev().kode)
                .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst());
        List<String> konsekvenserForYtelsen = new ArrayList<>();
        for (KodeDto kodeDto : dto.getKonsekvenserForYtelsen()) {
            konsekvenserForYtelsen.add(kodeDto.kode);
        }
        builder.medKonsekvenserForYtelsen(konsekvenserForYtelsen);
        return builder.build();
    }

    public static Builder ny() {
        return new Builder();
    }

    public String getVedtaksbrev() {
        return vedtaksbrev;
    }

    public boolean erInnvilget() {
        return BehandlingResultatType.INNVILGET.getKode().equals(behandlingResultatType);
    }

    public String getBehandlingResultatType() {
        return behandlingResultatType;
    }

    public String getAvslagsårsak() {
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

    public List<String> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public boolean isBehandlingsresultatAvslåttOrOpphørt() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType)
                || BehandlingResultatType.OPPHØR.equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatAvslått() {
        return BehandlingResultatType.AVSLÅTT.equals(behandlingResultatType);
    }

    public boolean erEndretForeldrepenger() {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.getKode().equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatOpphørt() {
        return BehandlingResultatType.OPPHØR.equals(behandlingResultatType);
    }

    public boolean erAvslått() {
        return BehandlingResultatType.AVSLÅTT.getKode().equals(behandlingResultatType);
    }

    public boolean isBehandlingsresultatInnvilget() {
        return BehandlingResultatType.INNVILGET.equals(behandlingResultatType);
    }

    public boolean erOpphørt() {
        return BehandlingResultatType.OPPHØR.getKode().equals(behandlingResultatType);
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


    public static final class Builder {
        private String avslagsårsak;
        private String fritekstbrev;
        private String overskrift;
        private String vedtaksbrev;
        private String behandlingResultatType;
        private String avslagarsakFritekst;
        private List<String> konsekvenserForYtelsen = new ArrayList<>();
        private String beregning;
        private VilkårResultat vilkårResultat;
        private String Periode;
        private BeregningResultat beregningResultat;
        private Behandling behandling;

        private Builder() {
        }

        public Builder medAvslagsårsak(String val) {
            avslagsårsak = val;
            return this;
        }

        public Builder medFritekstbrev(String val) {
            fritekstbrev = val;
            return this;
        }

        public Builder medOverskrift(String val) {
            overskrift = val;
            return this;
        }

        public Builder medVedtaksbrev(String val) {
            vedtaksbrev = val;
            return this;
        }

        public Builder medBehandlingResultatType(String val) {
            behandlingResultatType = val;
            return this;
        }

        public Builder medAvslagarsakFritekst(String val) {
            avslagarsakFritekst = val;
            return this;
        }

        public Builder medKonsekvenserForYtelsen(List<String> val) {
            konsekvenserForYtelsen = val;
            return this;
        }

        public Builder medBeregning(String val) {
            beregning = val;
            return this;
        }

        public Builder medVilkårResultat(VilkårResultat val) {
            vilkårResultat = val;
            return this;
        }

        public Builder medPeriode(String val) {
            Periode = val;
            return this;
        }

        public Builder medBeregningResultat(BeregningResultat val) {
            beregningResultat = val;
            return this;
        }

        public Builder medBehandling(Behandling val) {
            behandling = val;
            return this;
        }

        public Behandlingsresultat build() {
            return new Behandlingsresultat(this);
        }
    }
}

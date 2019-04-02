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

    public Behandlingsresultat(BehandlingsresultatDto dto) {
        if (dto.getAvslagsarsak() != null) {
            this.avslagsårsak = dto.getAvslagsarsak().kode;
        }
        if (dto.getType() != null) {
            this.behandlingResultatType = dto.getType().kode;
        }
        this.fritekstbrev = dto.getFritekstbrev();
        this.overskrift = dto.getOverskrift();
        this.vedtaksbrev = dto.getVedtaksbrev().kode;
        this.avslagarsakFritekst = dto.getAvslagsarsakFritekst();
        for (KodeDto kodeDto : dto.getKonsekvenserForYtelsen()) {
            konsekvenserForYtelsen.add(kodeDto.kode);
        }
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
}

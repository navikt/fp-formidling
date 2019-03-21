package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;

public class Behandlingsresultat {
    private String avslagsårsak; //Kode
    private String fritekstbrev;
    private String overskrift;
    private String behandligResultatType;
    private String avslagarsakFritekst;
    private String konsekvensForYtelsen; //Kode BehandlingsresultatKonsekvensForYtelsen

    //Objekter
    private String beregning; //BeregningResultat
    private String vilkårResultat; //VilkårResultat
    private String Periode; //Uttaksperiodegrense


    public Behandlingsresultat(BehandlingsresultatDto behandlingsresultatDto) {
        this.behandligResultatType = behandlingsresultatDto.getType().kode;
    }

    public String getBehandligResultatType() {
        return behandligResultatType;
    }
}

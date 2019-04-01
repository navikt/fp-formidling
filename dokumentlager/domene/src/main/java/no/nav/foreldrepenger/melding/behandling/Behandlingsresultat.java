package no.nav.foreldrepenger.melding.behandling;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class Behandlingsresultat {
    private String avslagsårsak; //Kode
    private String fritekstbrev;
    private String overskrift;
    private String vedtaksbrev;
    private String behandligResultatType;
    private String avslagarsakFritekst;
    private List<String> konsekvenserForYtelsen = new ArrayList<>(); //Kode BehandlingsresultatKonsekvensForYtelsen

    //Objekter
    private String beregning; //BeregningResultat
    private String vilkårResultat; //VilkårResultat
    private String Periode; //Uttaksperiodegrense

    public Behandlingsresultat(BehandlingsresultatDto dto) {
        if (dto.getAvslagsarsak() != null) {
            this.avslagsårsak = dto.getAvslagsarsak().kode;
        }
        if (dto.getType() != null) {
            this.behandligResultatType = dto.getType().kode;
        }
        this.fritekstbrev = dto.getFritekstbrev();
        this.overskrift = dto.getOverskrift();
        this.vedtaksbrev = dto.getVedtaksbrev().kode;
        this.avslagarsakFritekst = dto.getAvslagsarsakFritekst();
        for (KodeDto kodeDto : dto.getKonsekvenserForYtelsen()) {
            konsekvenserForYtelsen.add(kodeDto.kode);
        }
    }

    public String getBehandligResultatType() {
        return behandligResultatType;
    }

    public String getVedtaksbrev() {
        return vedtaksbrev;
    }

    public String getFritekstbrev() {
        return fritekstbrev;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public List<String> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }
}

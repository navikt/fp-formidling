package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingsresultatDto {

    private BehandlingResultatType type;
    private Avslagsårsak avslagsarsak;
    private List<KonsekvensForYtelsen> konsekvenserForYtelsen;
    private String avslagsarsakFritekst;
    private String overskrift;
    private String fritekstbrev;
    private SkjæringstidspunktDto skjæringstidspunkt;
    private boolean endretDekningsgrad;

    public BehandlingResultatType getType() {
        return type;
    }

    public void setType(BehandlingResultatType type) {
        this.type = type;
    }

    public Avslagsårsak getAvslagsarsak() {
        return avslagsarsak;
    }

    public void setAvslagsarsak(Avslagsårsak avslagsarsak) {
        this.avslagsarsak = avslagsarsak;
    }

    public List<KonsekvensForYtelsen> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen != null ? konsekvenserForYtelsen : List.of();
    }

    public void setKonsekvenserForYtelsen(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        this.konsekvenserForYtelsen = konsekvenserForYtelsen;
    }

    public String getAvslagsarsakFritekst() {
        return avslagsarsakFritekst;
    }

    public void setAvslagsarsakFritekst(String avslagsarsakFritekst) {
        this.avslagsarsakFritekst = avslagsarsakFritekst;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public void setOverskrift(String overskrift) {
        this.overskrift = overskrift;
    }

    public String getFritekstbrev() {
        return fritekstbrev;
    }

    public void setFritekstbrev(String fritekstbrev) {
        this.fritekstbrev = fritekstbrev;
    }

    public LocalDate getSkjæringstidspunkt() {
        return Optional.ofNullable(skjæringstidspunkt).map(SkjæringstidspunktDto::dato).orElse(null);
    }

    public boolean utenMinsterett() {
        return Optional.ofNullable(skjæringstidspunkt).map(SkjæringstidspunktDto::utenMinsterett).orElse(true);
    }

    public boolean endretDekningsgrad() {
        return endretDekningsgrad;
    }

    public void setEndretDekningsgrad(boolean endretDekningsgrad) {
        this.endretDekningsgrad = endretDekningsgrad;
    }

    public void setSkjæringstidspunkt(SkjæringstidspunktDto skjæringstidspunkt) {
        this.skjæringstidspunkt = skjæringstidspunkt;
    }

    @Override
    public String toString() {
        return "BehandlingsresultatDto{type=" + (type != null ? type.toString() : null) + ", avslagsarsak=" + (avslagsarsak != null ? avslagsarsak.toString() : null)
            + ", konsekvenserForYtelsen=" + (konsekvenserForYtelsen != null ? konsekvenserForYtelsen.toString() : null) + ", avslagsarsakFritekst='"
            + avslagsarsakFritekst + '\'' + ", overskrift='" + overskrift + '\'' + ", fritekstbrev='" + fritekstbrev + '\''
            + ", skjæringstidspunkt=" + skjæringstidspunkt + '\'' + '}';
    }
}

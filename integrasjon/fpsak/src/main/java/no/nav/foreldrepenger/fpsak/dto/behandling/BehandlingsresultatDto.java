package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingsresultatDto {
    private Integer id;
    private BehandlingResultatType type;
    private Avslagsårsak avslagsarsak;
    private List<KonsekvensForYtelsen> konsekvenserForYtelsen;
    private Vedtaksbrev vedtaksbrev;
    private String avslagsarsakFritekst;
    private String overskrift;
    private String fritekstbrev;
    private Boolean erRevurderingMedUendretUtfall;
    private SkjæringstidspunktDto skjæringstidspunkt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Vedtaksbrev getVedtaksbrev() {
        return vedtaksbrev;
    }

    public void setVedtaksbrev(Vedtaksbrev vedtaksbrev) {
        this.vedtaksbrev = vedtaksbrev;
    }

    public Boolean getErRevurderingMedUendretUtfall() {
        return Boolean.TRUE.equals(erRevurderingMedUendretUtfall);
    }

    public void setErRevurderingMedUendretUtfall(Boolean erRevurderingMedUendretUtfall) {
        this.erRevurderingMedUendretUtfall = erRevurderingMedUendretUtfall;
    }

    public LocalDate getSkjæringstidspunkt() {
        if (skjæringstidspunkt!=null) {
            return skjæringstidspunkt.dato();
        }
        return null;
    }

    public void setSkjæringstidspunkt(SkjæringstidspunktDto skjæringstidspunkt) {
        this.skjæringstidspunkt = skjæringstidspunkt;
    }

    @Override
    public String toString() {
        return "BehandlingsresultatDto{" +
                "id=" + id +
                ", type=" + (type != null ? type.toString() : null) +
                ", avslagsarsak=" + (avslagsarsak != null ? avslagsarsak.toString() : null) +
                ", vedtaksbrev=" + (vedtaksbrev != null ? vedtaksbrev.toString() : null) +
                ", konsekvenserForYtelsen=" + (konsekvenserForYtelsen != null ? konsekvenserForYtelsen.toString() : null) +
                ", avslagsarsakFritekst='" + avslagsarsakFritekst + '\'' +
                ", overskrift='" + overskrift + '\'' +
                ", fritekstbrev='" + fritekstbrev + '\'' +
                ", erRevurderingMedUendretUtfall='" + erRevurderingMedUendretUtfall + '\'' +
                ", skjæringstidspunkt=" + skjæringstidspunkt + '\'' +
                '}';
    }
}

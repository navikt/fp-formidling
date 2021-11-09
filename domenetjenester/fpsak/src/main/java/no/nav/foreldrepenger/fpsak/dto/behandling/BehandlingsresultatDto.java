package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingsresultatDto {
    private Integer id;
    private KodeDto type;
    private KodeDto avslagsarsak;
    private KodeDto rettenTil;
    private List<KodeDto> konsekvenserForYtelsen;
    private KodeDto vedtaksbrev;
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

    public KodeDto getType() {
        return type;
    }

    public void setType(KodeDto type) {
        this.type = type;
    }

    public KodeDto getAvslagsarsak() {
        return avslagsarsak;
    }

    public void setAvslagsarsak(KodeDto avslagsarsak) {
        this.avslagsarsak = avslagsarsak;
    }

    public KodeDto getRettenTil() {
        return rettenTil;
    }

    public void setRettenTil(KodeDto rettenTil) {
        this.rettenTil = rettenTil;
    }

    public List<KodeDto> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public void setKonsekvenserForYtelsen(List<KodeDto> konsekvenserForYtelsen) {
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

    public KodeDto getVedtaksbrev() {
        return vedtaksbrev;
    }

    public void setVedtaksbrev(KodeDto vedtaksbrev) {
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
                ", rettenTil=" + rettenTil +
                ", konsekvenserForYtelsen=" + (konsekvenserForYtelsen != null ? konsekvenserForYtelsen.toString() : null) +
                ", avslagsarsakFritekst='" + avslagsarsakFritekst + '\'' +
                ", overskrift='" + overskrift + '\'' +
                ", fritekstbrev='" + fritekstbrev + '\'' +
                ", erRevurderingMedUendretUtfall='" + erRevurderingMedUendretUtfall + '\'' +
                ", skjæringstidspunkt=" + skjæringstidspunkt + '\'' +
                '}';
    }
}

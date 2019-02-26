package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingsresultatDto {
    private Integer id;
    private Kode type;
    private Kode avslagsarsak;
    private Kode rettenTil;
    private List<Kode> konsekvenserForYtelsen;
    private Kode vedtaksbrev;
    private String avslagsarsakFritekst;
    private String overskrift;
    private String fritekstbrev;
    private LocalDate skjaeringstidspunktForeldrepenger;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Kode getType() {
        return type;
    }

    public void setType(Kode type) {
        this.type = type;
    }

    public Kode getAvslagsarsak() {
        return avslagsarsak;
    }

    public void setAvslagsarsak(Kode avslagsarsak) {
        this.avslagsarsak = avslagsarsak;
    }

    public Kode getRettenTil() {
        return rettenTil;
    }

    public void setRettenTil(Kode rettenTil) {
        this.rettenTil = rettenTil;
    }

    public List<Kode> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public void setKonsekvenserForYtelsen(List<Kode> konsekvenserForYtelsen) {
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

    public LocalDate getSkjaeringstidspunktForeldrepenger() {
        return skjaeringstidspunktForeldrepenger;
    }

    public void setSkjaeringstidspunktForeldrepenger(LocalDate skjaeringstidspunktForeldrepenger) {
        this.skjaeringstidspunktForeldrepenger = skjaeringstidspunktForeldrepenger;
    }

    public Kode getVedtaksbrev() {
        return vedtaksbrev;
    }

    public void setVedtaksbrev(Kode vedtaksbrev) {
        this.vedtaksbrev = vedtaksbrev;
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
                ", skjaeringstidspunktForeldrepenger='" + skjaeringstidspunktForeldrepenger + '\'' +
                '}';
    }
}

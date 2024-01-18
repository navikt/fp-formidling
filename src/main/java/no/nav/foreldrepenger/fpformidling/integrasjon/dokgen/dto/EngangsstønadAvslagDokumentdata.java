package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

import java.util.List;
import java.util.Objects;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EngangsstønadAvslagDokumentdata extends Dokumentdata {
    private String avslagÅrsak;
    private boolean førstegangsbehandling;
    private int antallBarn;
    private String relasjonsRolle;
    private boolean gjelderFødsel;
    private List<String> vilkårTyper;
    private int klagefristUker;
    private String avslagMedlemskap;

    public String getAvslagÅrsak() {
        return avslagÅrsak;
    }

    public boolean getFørstegangsbehandling() {
        return førstegangsbehandling;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public String getRelasjonsRolle() {
        return relasjonsRolle;
    }

    public boolean getGjelderFødsel() {
        return gjelderFødsel;
    }

    public List<String> getVilkårTyper() {
        return vilkårTyper;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (EngangsstønadAvslagDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(avslagÅrsak, that.avslagÅrsak) && Objects.equals(førstegangsbehandling,
            that.førstegangsbehandling) && Objects.equals(antallBarn, that.antallBarn) && Objects.equals(relasjonsRolle, that.relasjonsRolle)
            && Objects.equals(gjelderFødsel, that.gjelderFødsel) && Objects.equals(vilkårTyper, that.vilkårTyper) && Objects.equals(klagefristUker,
            that.klagefristUker) && Objects.equals(avslagMedlemskap, that.avslagMedlemskap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, avslagÅrsak, førstegangsbehandling, antallBarn, relasjonsRolle, gjelderFødsel, vilkårTyper, klagefristUker,
            avslagMedlemskap);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private EngangsstønadAvslagDokumentdata kladd;

        private Builder() {
            this.kladd = new EngangsstønadAvslagDokumentdata();
        }

        public EngangsstønadAvslagDokumentdata.Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medAvslagsÅrsak(String avslagsÅrsak) {
            this.kladd.avslagÅrsak = avslagsÅrsak;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medFørstegangsbehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medRelasjonsRolle(String relasjonsRolle) {
            this.kladd.relasjonsRolle = relasjonsRolle;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medGjelderFødsel(boolean gjelderFødsel) {
            this.kladd.gjelderFødsel = gjelderFødsel;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medVilkårTyper(List<String> vilkårTyper) {
            this.kladd.vilkårTyper = vilkårTyper;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medAvslagMedlemskap(String avslagMedlemskap) {
            this.kladd.avslagMedlemskap = avslagMedlemskap;
            return this;
        }

        public EngangsstønadAvslagDokumentdata build() {
            return this.kladd;
        }
    }
}

package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.List;

public class EngangsstønadAvslagDokumentdata extends Dokumentdata {
    private String avslagÅrsak;
    private boolean førstegangsbehandling;
    private int antallBarn;
    private String relasjonsRolle;
    private boolean gjelderFødsel;
    private List<String> vilkårTyper;
    private int klagefristUker;
    private String avslagMedlemskap;

    public static Builder ny() {
        return new Builder();
    }

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

    public boolean getgjelderFødsel() {
        return gjelderFødsel;
    }

    public List<String> getvilkårTyper() {
        return vilkårTyper;
    }

    public int getKlagefristUker() { return klagefristUker; }

    public String getAvslagMedlemskap() { return avslagMedlemskap; }

    public static class Builder {
        private EngangsstønadAvslagDokumentdata kladd;

        private Builder() {
            this.kladd = new EngangsstønadAvslagDokumentdata();
        }

        public EngangsstønadAvslagDokumentdata.Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medAvslagsÅrsaker(String avslagsÅrsak) {
            this.kladd.avslagÅrsak = avslagsÅrsak;
            return this;
        }

        public EngangsstønadAvslagDokumentdata.Builder medFørstegangBehandling(boolean førstegangsbehandling) {
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

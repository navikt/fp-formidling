package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.List;

public class EngangsstønadAvslagDokumentData extends Dokumentdata {
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
        private EngangsstønadAvslagDokumentData kladd;

        private Builder() {
            this.kladd = new EngangsstønadAvslagDokumentData();
        }

        public EngangsstønadAvslagDokumentData.Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medAvslagsÅrsaker(String avslagsÅrsak) {
            this.kladd.avslagÅrsak = avslagsÅrsak;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medFørstegangBehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medRelasjonsRolle(String relasjonsRolle) {
            this.kladd.relasjonsRolle = relasjonsRolle;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medGjelderFødsel(boolean gjelderFødsel) {
            this.kladd.gjelderFødsel = gjelderFødsel;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medVilkårTyper(List<String> vilkårTyper) {
            this.kladd.vilkårTyper = vilkårTyper;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public EngangsstønadAvslagDokumentData.Builder medAvslagMedlemskap(String avslagMedlemskap) {
            this.kladd.avslagMedlemskap = avslagMedlemskap;
            return this;
        }

        public EngangsstønadAvslagDokumentData build() {
            return this.kladd;
        }
    }
}

package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeldrepengerAnnullertDokumentdata extends Dokumentdata {
    private boolean harSøktOmNyPeriode;
    private String planlagtOppstartDato;
    private String kanBehandlesDato;
    private int klagefristUker;
    private AnnulleringÅrsak annulleringÅrsak;

    public boolean getHarSøktOmNyPeriode() {
        return harSøktOmNyPeriode;
    }

    public String getPlanlagtOppstartDato() {
        return planlagtOppstartDato;
    }

    public String getKanBehandlesDato() {
        return kanBehandlesDato;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public AnnulleringÅrsak getAnnulleringÅrsak() {
        return annulleringÅrsak;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (ForeldrepengerAnnullertDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(harSøktOmNyPeriode, that.harSøktOmNyPeriode) && Objects.equals(
            planlagtOppstartDato, that.planlagtOppstartDato) && Objects.equals(kanBehandlesDato, that.kanBehandlesDato) && Objects.equals(
            klagefristUker, that.klagefristUker) && Objects.equals(annulleringÅrsak, that.annulleringÅrsak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, harSøktOmNyPeriode, planlagtOppstartDato, kanBehandlesDato, klagefristUker, annulleringÅrsak);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private ForeldrepengerAnnullertDokumentdata kladd;

        private Builder() {
            this.kladd = new ForeldrepengerAnnullertDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medHarSøktOmNyPeriode(boolean harSøktOmNyPeriode) {
            this.kladd.harSøktOmNyPeriode = harSøktOmNyPeriode;
            return this;
        }

        public Builder medPlanlagtOppstartDato(String planlagtOppstartDato) {
            this.kladd.planlagtOppstartDato = planlagtOppstartDato;
            return this;
        }

        public Builder medKanBehandlesDato(String kanBehandlesDato) {
            this.kladd.kanBehandlesDato = kanBehandlesDato;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medAnnulleringÅrsak(AnnulleringÅrsak annulleringÅrsak) {
            this.kladd.annulleringÅrsak = annulleringÅrsak;
            return this;
        }

        public ForeldrepengerAnnullertDokumentdata build() {
            return this.kladd;
        }
    }

    public enum AnnulleringÅrsak {
        BRUKER_HAR_SØKT_OM_NY_PERIODE,
        BRUKER_MOTTAR_PLEIEPENGER,
        ANNEN_PART_OVERTAR_UTTAKET,
        MANUELL_SAKSBEHANDLING
        ;
    }
}

package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;

@Deprecated
public class DokumentBehandlingsresultatDto {
    private String fritekst;
    private String konsekvensForYtelse;
    private String behandlingsResultat;
    //Innvilgelse
    private Long beløp;
    private Long differanse;
    //Avslag og opphør
    private String vilkårTypeKode;
    private Set<String> avslagsårsak = new LinkedHashSet<>();
    private Set<String> lovhjemlerForAvslag = new TreeSet<>(new LovhjemmelComparator());
    private LocalDate dodsdato;
    //Fritekstbrev
    private String overskrift;
    private String brødtekst;

    public Optional<String> getFritekst() {
        return Optional.ofNullable(fritekst);
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public String getKonsekvensForYtelse() {
        return konsekvensForYtelse;
    }

    public void setKonsekvensForYtelse(String konsekvensForYtelse) {
        this.konsekvensForYtelse = konsekvensForYtelse;
    }

    public String getBehandlingsResultat() {
        return behandlingsResultat;
    }

    public void setBehandlingsResultat(String behandlingsResultat) {
        this.behandlingsResultat = behandlingsResultat;
    }

    public Optional<Long> getBeløp() {
        return Optional.ofNullable(beløp);
    }

    public void setBeløp(Long beløp) {
        this.beløp = beløp;
    }

    public Optional<Long> getDifferanse() {
        return Optional.ofNullable(differanse);
    }

    public void setDifferanse(Long differanse) {
        this.differanse = differanse;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public void setOverskrift(String overskrift) {
        this.overskrift = overskrift;
    }

    public String getBrødtekst() {
        return brødtekst;
    }

    public void setBrødtekst(String brødtekst) {
        this.brødtekst = brødtekst;
    }

    public String getAvslagsårsak() {
        return String.join(",", avslagsårsak);
    }

    public Set<String> getAvslagsårsakListe() {
        return avslagsårsak;
    }

    public void leggTilAvslagsårsak(String avslagsÅrsak) {
        this.avslagsårsak.add(avslagsÅrsak);
    }

    public String getVilkårTypeKode() {
        return vilkårTypeKode;
    }

    public void setVilkårTypeKode(String vilkårTypeKode) {
        this.vilkårTypeKode = vilkårTypeKode;
    }

    public Set<String> getLovhjemlerForAvslag() {
        return lovhjemlerForAvslag;
    }

    public void leggTilLovhjemlerForAvslag(Set<String> lovhjemler) {
        lovhjemlerForAvslag.addAll(lovhjemler);
    }

    public Optional<LocalDate> getDodsdato() {
        return Optional.ofNullable(dodsdato);
    }

    public void setDodsdato(LocalDate dodsdato) {
        this.dodsdato = dodsdato;
    }
}

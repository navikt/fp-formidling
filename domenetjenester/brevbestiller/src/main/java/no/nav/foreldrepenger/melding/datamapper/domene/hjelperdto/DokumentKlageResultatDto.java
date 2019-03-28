package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;

@Deprecated
public class DokumentKlageResultatDto {

    private List<String> avvistGrunnListe = new ArrayList<>();
    private Set<String> lovhjemler = new TreeSet<>(new LovhjemmelComparator());
    private Integer antallAvvistGrunn = 0;

    public List<String> getAvvistGrunnListe() {
        return avvistGrunnListe;
    }

    public void addAvvistGrunn(String avvistGrunn) {
        this.avvistGrunnListe.add(avvistGrunn);
    }

    public Integer getAntallAvvistGrunn() {
        return antallAvvistGrunn;
    }

    public void setAntallAvvistGrunn(Integer antallAvvistGrunn) {
        this.antallAvvistGrunn = antallAvvistGrunn;
    }

    public Set<String> getLovhjemler() {
        return lovhjemler;
    }

    public void addLovhjemler(Set<String> lovhjemler) {
        this.lovhjemler.addAll(lovhjemler);
    }
}

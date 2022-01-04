package no.nav.foreldrepenger.melding.behandling.innsyn;

import java.util.List;

public class Innsyn {
    private InnsynResultatType innsynResultatType;
    //Kun dokumenter der bruker fikk innsyn
    private List<InnsynDokument> innsynDokumenter;

    public Innsyn(InnsynResultatType innsynResultatType,
                  List<InnsynDokument> innsynDokumenter) {
        this.innsynResultatType = innsynResultatType;
        this.innsynDokumenter = innsynDokumenter;
    }

    public InnsynResultatType getInnsynResultatType() {
        return innsynResultatType;
    }

    public List<InnsynDokument> getInnsynDokumenter() {
        return innsynDokumenter;
    }
}

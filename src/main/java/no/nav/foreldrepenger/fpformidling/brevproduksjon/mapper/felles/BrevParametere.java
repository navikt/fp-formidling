package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.Period;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;

@ApplicationScoped
public class BrevParametere {

    private Integer klagefristUker;
    private Integer klagefristUkerInnsyn;
    private Integer svarfristDager;
    private Integer søkAntallUker;

    @Inject
    public BrevParametere(@KonfigVerdi(value = "vedtak.klagefrist.uker", defaultVerdi = "6") Integer klagefrist,
                          @KonfigVerdi(value = "innsyn.klagefrist.uker", defaultVerdi = "3") Integer klagefristInnsyn,
                          @KonfigVerdi(value = "brev.svarfrist.dager", defaultVerdi = "P3W") Period svarfrist,
                          @KonfigVerdi(value = "søk.antall.uker", defaultVerdi = "P4W") Period søkAntallUker) {
        this.klagefristUker = klagefrist;
        this.klagefristUkerInnsyn = klagefristInnsyn;
        this.svarfristDager = svarfrist.getDays();
        this.søkAntallUker = (søkAntallUker.getDays() / 7);
    }

    public BrevParametere() {
        //for CDI
    }

    public Integer getKlagefristUker() {
        return klagefristUker;
    }

    public Integer getKlagefristUkerInnsyn() {
        return klagefristUkerInnsyn;
    }

    public Integer getSvarfristDager() {
        return svarfristDager;
    }

    public Integer getSøkAntallUker() {
        return søkAntallUker;
    }
}

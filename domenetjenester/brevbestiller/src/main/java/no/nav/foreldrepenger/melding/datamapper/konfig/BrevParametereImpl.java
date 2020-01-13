package no.nav.foreldrepenger.melding.datamapper.konfig;

import java.time.Period;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BrevParametereImpl implements BrevParametere {

    private Integer klagefristUker;
    private Integer klagefristUkerInnsyn;
    private Integer svarfristDager;
    private Integer søkAntallUker;

    @Inject
    public BrevParametereImpl(
        @KonfigVerdi(value = "vedtak.klagefrist.uker", defaultVerdi = "6") Integer klagefrist,
        @KonfigVerdi(value = "innsyn.klagefrist.uker", defaultVerdi = "3") Integer klagefristInnsyn,
        @KonfigVerdi(value = "brev.svarfrist.dager", defaultVerdi = "P3W") Period svarfrist,
        @KonfigVerdi(value = "søk.antall.uker", defaultVerdi = "P4W") Period søkAntallUker) {
        this.klagefristUker = klagefrist;
        this.klagefristUkerInnsyn = klagefristInnsyn;
        this.svarfristDager = svarfrist.getDays();
        this.søkAntallUker = (søkAntallUker.getDays() / 7);
    }

    public BrevParametereImpl() {
        //for CDI
    }

    @Override
    public Integer getKlagefristUker() {
        return klagefristUker;
    }

    @Override
    public Integer getKlagefristUkerInnsyn() {
        return klagefristUkerInnsyn;
    }

    @Override
    public Integer getSvarfristDager() {
        return svarfristDager;
    }

    @Override
    public Integer getSøkAntallUker() {
        return søkAntallUker;
    }
}

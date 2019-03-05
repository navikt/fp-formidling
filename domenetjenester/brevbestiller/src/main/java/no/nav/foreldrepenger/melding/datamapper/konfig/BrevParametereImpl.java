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
        @KonfigVerdi("vedtak.klagefrist.uker") Integer klagefrist,
        @KonfigVerdi("innsyn.klagefrist.uker") Integer klagefristInnsyn,
        @KonfigVerdi("brev.svarfrist.dager") Period svarfrist,
        @KonfigVerdi("søk.antall.uker") Period søkAntallUker) {
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

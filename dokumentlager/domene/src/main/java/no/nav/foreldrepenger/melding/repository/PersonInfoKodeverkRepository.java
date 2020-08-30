package no.nav.foreldrepenger.melding.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class PersonInfoKodeverkRepository {
    private KodeverkRepository kodeverkRepository;

    PersonInfoKodeverkRepository() {
        // for CDI proxy
    }

    @Inject
    public PersonInfoKodeverkRepository(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public NavBrukerKjønn finnBrukerKjønn(String kode) {
        return kodeverkRepository.finn(NavBrukerKjønn.class, kode);
    }

    public PersonstatusType finnPersonstatus(String kode) {
        return kodeverkRepository.finn(PersonstatusType.class, kode);
    }
}

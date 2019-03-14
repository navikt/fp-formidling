package no.nav.foreldrepenger.melding.geografisk;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class SpråkKodeverkRepositoryImpl implements SpråkKodeverkRepository {

    private KodeverkRepository kodeverkRepository;

    SpråkKodeverkRepositoryImpl() {
        // for CDI proxy
    }

    @Inject
    public SpråkKodeverkRepositoryImpl(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
        Objects.requireNonNull(kodeverkRepository, "kodeverkRepository"); //$NON-NLS-1$
    }

    @Override
    public Optional<Språkkode> finnSpråkMedKodeverkEiersKode(String kodeverkEiersKode) {
        try {
            return Optional.of(kodeverkRepository.finnForKodeverkEiersKode(Språkkode.class, kodeverkEiersKode));
        } catch (TekniskException e) { //NOSONAR
            return Optional.empty();
        }
    }
}

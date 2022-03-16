package no.nav.foreldrepenger.fpformidling.kafkatjenester.dokumentbestilling;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.hendelser.HendelseRepository;

@ApplicationScoped
public class DokumentHendelseTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokumentHendelseTjeneste.class);
    private HendelseRepository hendelseRepository;

    public DokumentHendelseTjeneste() {
        //CDI
    }

    @Inject
    public DokumentHendelseTjeneste(HendelseRepository hendelseRepository) {
        this.hendelseRepository = hendelseRepository;
    }

    /**
     * Lagres ikke om en hendelse med samme bestillingsUuid finnes.
     *
     * @param hendelse DokumentHendelse som skal lagres
     * @return lagret DokumentHendelse eller empty Optional
     */
    public Optional<DokumentHendelse> validerUnikOgLagre(DokumentHendelse hendelse) {
        if (hendelseRepository.finnesHendelseMedUuidAllerede(hendelse.getBestillingUuid())) {
            LOGGER.info("Lagrer ikke hendelse med duplikat bestillingUuid: {} for behandling: {} OK", hendelse.getBestillingUuid(), hendelse.getBehandlingUuid());
            return Optional.empty();
        }
        hendelseRepository.lagre(hendelse);
        LOGGER.info("Lagret hendelse: {} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
        return Optional.of(hendelse);
    }
}

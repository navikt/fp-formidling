package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.RevurderingVarselÅrsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokumentHendelseTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentHendelseTjeneste.class);
    private DokumentHendelseRepository dokumentHendelseRepository;

    DokumentHendelseTjeneste() {
        //CDI
    }

    @Inject
    public DokumentHendelseTjeneste(DokumentHendelseRepository dokumentHendelseRepository) {
        this.dokumentHendelseRepository = dokumentHendelseRepository;
    }

    public record DokumentBestilling(UUID behandlingUuid,
                                     UUID bestillingUUid,
                                     DokumentMalType dokumentMal,
                                     DokumentMalType journalførSom,
                                     RevurderingVarselÅrsak revurderingVarselÅrsak,
                                     String fritekst,
                                     String tittel) {
    }

    /**
     * Lagres ikke om en hendelse med samme bestillingsUuid finnes.
     *
     * @param hendelse DokumentHendelse som skal lagres
     * @return lagret DokumentHendelse eller empty Optional
     */
    public Optional<DokumentHendelseEntitet> validerUnikOgLagre(DokumentHendelseEntitet hendelse) {
        if (dokumentHendelseRepository.finnesHendelseMedUuidAllerede(hendelse.getBestillingUuid())) {
            LOG.info("Lagrer ikke hendelse med duplikat bestillingUuid: {} for behandling: {} OK", hendelse.getBestillingUuid(),
                hendelse.getBehandlingUuid());
            return Optional.empty();
        }
        dokumentHendelseRepository.lagre(hendelse);
        LOG.info("Lagret hendelse: {} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
        return Optional.of(hendelse);
    }

    public Optional<DokumentHendelseEntitet> hentHendelse(Long id) {
        return Optional.ofNullable(dokumentHendelseRepository.hentDokumentHendelseMedId(id));
    }
}

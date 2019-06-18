package no.nav.foreldrepenger.melding.hendelser;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;

public interface HendelseRepository {

    void lagre(DokumentHendelse dokumentHendelse);

    boolean finnesHendelseMedUuidAllerede(UUID bestillingUuid);

    void lagre(EventmottakFeillogg eventmottakFeillogg);

    DokumentHendelse hentDokumentHendelseMedId(long id);

    List<DokumentHendelse> hentDokumentHendelserForBehandling(UUID behandlingUuid);

    boolean erDokumentHendelseMottatt(UUID behanlingUuid, String dokumentMal);

}

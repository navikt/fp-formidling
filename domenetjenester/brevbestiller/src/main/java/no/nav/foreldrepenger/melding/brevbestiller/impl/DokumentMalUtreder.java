package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;

@ApplicationScoped
class DokumentMalUtreder {

    private KodeverkTabellRepository kodeverkTabellRepository;

    public DokumentMalUtreder() {
        //CDI
    }

    @Inject
    public DokumentMalUtreder(KodeverkTabellRepository kodeverkTabellRepository) {
        this.kodeverkTabellRepository = kodeverkTabellRepository;
    }

    private static DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandlingDto, DokumentHendelse hendelse) {
        //TODO
        return null;
    }

    private static DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        //TODO
        return null;
    }

    DokumentMalType utredDokumentmal(Behandling behandling, DokumentHendelse hendelse) {
        if (hendelse.getDokumentMalType() != null) {
            return hendelse.getDokumentMalType();
        }
        if (hendelse.isGjelderVedtak()) {
            return utledVedtaksbrev(behandling, hendelse);
        }
        throw new IllegalStateException("Klarer ikke utlede dokumentmal");
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling, hendelse);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling, hendelse);
        }
        throw new IllegalStateException("Finner ikke ytelse: " + hendelse.getYtelseType());
    }

}

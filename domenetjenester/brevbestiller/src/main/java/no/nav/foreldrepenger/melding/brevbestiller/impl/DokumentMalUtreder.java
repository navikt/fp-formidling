package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.vedtak.util.StringUtils;

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

    private static DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandlingDto, DokumentHendelseDto dto) {
        return null;
    }

    private static DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling, DokumentHendelseDto dto) {
        return null;
    }

    DokumentMalType utredDokumentmal(Behandling behandling, DokumentHendelseDto dto) {
        if (dto.isGjelderVedtak()) {
            return utledVedtaksbrev(behandling, dto);
        }
        if (!StringUtils.nullOrEmpty(dto.getDokumentMal())) {
            return kodeverkTabellRepository.finnDokumentMalType(dto.getDokumentMal());
        }
        return null;
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelseDto dto) {
        if (FagsakYtelseType.FORELDREPENGER.getKode().equals(dto.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling, dto);
        } else if (FagsakYtelseType.ENGANGSTØNAD.getKode().equals(dto.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling, dto);
        }
        throw new IllegalStateException("Finner ikke ytelse: " + dto.getYtelseType());
    }

}

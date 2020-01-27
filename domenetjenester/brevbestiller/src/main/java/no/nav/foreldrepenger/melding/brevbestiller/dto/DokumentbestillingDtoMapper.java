package no.nav.foreldrepenger.melding.brevbestiller.dto;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class DokumentbestillingDtoMapper {
    private KodeverkRepository kodeverkRepository;
    private DokumentRepository dokumentRepository;

    public DokumentbestillingDtoMapper() {
        //CDI
    }

    @Inject
    public DokumentbestillingDtoMapper(KodeverkRepository kodeverkRepository, DokumentRepository dokumentRepository) {
        this.kodeverkRepository = kodeverkRepository;
        this.dokumentRepository = dokumentRepository;
    }

    public DokumentHendelse mapDokumentbestillingFraDtoForEndepunkt(DokumentbestillingDto brevDto) {
        return new DokumentHendelse.Builder()
                .medBehandlingUuid(brevDto.getBehandlingUuid())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(utledYtelseType(brevDto.getYtelseType().getKode()))
                .medFritekst(brevDto.getFritekst())
                .medTittel(brevDto.getTittel())
                .medHistorikkAktør(utledHistorikkAktør(brevDto.getHistorikkAktør()))
                .medDokumentMalType(utleddokumentMalType(brevDto.getDokumentMal()))
                .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(brevDto.getArsakskode()))
                .medGjelderVedtak(brevDto.isGjelderVedtak())
                .medVedtaksbrev(VedtaksbrevMapper.tilEntitet(brevDto.getVedtaksbrev()))
                .build();
    }

    private RevurderingVarslingÅrsak utledRevurderingVarslingsårsak(String varslingsårsak) {
        if (StringUtils.nullOrEmpty(varslingsårsak)) {
            return RevurderingVarslingÅrsak.UDEFINERT;
        }
        return kodeverkRepository.finn(RevurderingVarslingÅrsak.class, varslingsårsak);
    }

    private FagsakYtelseType utledYtelseType(String ytelseType) {
        if (StringUtils.nullOrEmpty(ytelseType)) {
            return null;
        }
        return kodeverkRepository.finn(FagsakYtelseType.class, ytelseType);
    }

    private DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (StringUtils.nullOrEmpty(dokumentmal)) {
            return null;
        }
        return dokumentRepository.hentDokumentMalType(dokumentmal);
    }

    private HistorikkAktør utledHistorikkAktør(String historikkAktør) {
        if (StringUtils.nullOrEmpty(historikkAktør)) {
            return HistorikkAktør.UDEFINERT;
        }
        return kodeverkRepository.finn(HistorikkAktør.class, historikkAktør);
    }
}

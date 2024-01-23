package no.nav.foreldrepenger.fpformidling.tjenester;

import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.YtelseType;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingV2Dto;

public final class DokumentHendelseDtoMapper {

    private DokumentHendelseDtoMapper() {
    }

    public static DokumentHendelse mapFra(DokumentbestillingV2Dto dokumentbestilling) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(dokumentbestilling.behandlingUuid())
            .medBestillingUuid(dokumentbestilling.dokumentbestillingUuid())
            .medYtelseType(utledYtelseType(dokumentbestilling.ytelseType()))
            .medFritekst(dokumentbestilling.fritekst())
            .medDokumentMalType(utleddokumentMalType(dokumentbestilling.dokumentMal()))
            .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(dokumentbestilling.arsakskode()))
            .build();
    }

    public static DokumentHendelse mapFra(DokumentbestillingDto brevDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(brevDto.getBehandlingUuid())
            .medBestillingUuid(brevDto.getDokumentbestillingUuid() != null ? brevDto.getDokumentbestillingUuid() : UUID.randomUUID())
            .medYtelseType(utledYtelseType(brevDto.getFagsakYtelseType()))
            .medFritekst(brevDto.getFritekst())
            .medTittel(brevDto.getTittel())
            .medDokumentMalType(utleddokumentMalType(brevDto.getDokumentMal()))
            .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(brevDto.getArsakskode()))
            .medGjelderVedtak(brevDto.isGjelderVedtak())
            .medVedtaksbrev(utledVedtaksbrev(brevDto.getAutomatiskVedtaksbrev()))
            .build();
    }

    private static RevurderingVarslingÅrsak utledRevurderingVarslingsårsak(String varslingsårsak) {
        if (varslingsårsak == null || varslingsårsak.isEmpty()) {
            return RevurderingVarslingÅrsak.UDEFINERT;
        }
        return RevurderingVarslingÅrsak.fraKode(varslingsårsak);
    }

    private static FagsakYtelseType utledYtelseType(YtelseType ytelseType) {
        if (ytelseType == null) {
            return null;
        }
        return switch (ytelseType) {
            case ES -> FagsakYtelseType.ENGANGSTØNAD;
            case FP -> FagsakYtelseType.FORELDREPENGER;
            case SVP -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private static Vedtaksbrev utledVedtaksbrev(Boolean automatiskBrev) {
        return Optional.ofNullable(automatiskBrev).filter(ab -> ab).map(ab -> Vedtaksbrev.AUTOMATISK).orElse(Vedtaksbrev.UDEFINERT);
    }

    private static DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (dokumentmal == null || dokumentmal.isEmpty()) {
            return null;
        }
        return DokumentMalType.fraKode(dokumentmal);
    }
}

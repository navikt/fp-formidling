package no.nav.foreldrepenger.fpformidling.kafkatjenester.dokumentbestilling;

import no.nav.foreldrepenger.fpformidling.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkAktør;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.YtelseType;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingV2Dto;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;

public class DokumentHendelseDtoMapper {

    private static RevurderingVarslingÅrsak utledRevurderingVarslingsårsak(String varslingsårsak) {
        if (varslingsårsak == null || varslingsårsak.isEmpty()) {
            return RevurderingVarslingÅrsak.UDEFINERT;
        }
        return RevurderingVarslingÅrsak.fraKode(varslingsårsak);
    }

    private static FagsakYtelseType utledYtelseType(String ytelseType) {
        if (ytelseType == null || ytelseType.isEmpty()) {
            return null;
        }
        return FagsakYtelseType.fraKode(ytelseType);
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

    private static DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (dokumentmal == null || dokumentmal.isEmpty()) {
            return null;
        }
        return DokumentMalType.fraKode(dokumentmal);
    }

    private static HistorikkAktør utledHistorikkAktør(String historikkAktør) {
        if (historikkAktør == null || historikkAktør.isEmpty()) {
            return HistorikkAktør.UDEFINERT;
        }
        return HistorikkAktør.fraKode(historikkAktør);
    }

    public static DokumentHendelse mapDokumentHendelseFraDtoForKafka(DokumentbestillingV1 dokumentbestilling) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(dokumentbestilling.getBehandlingUuid())
                .medBestillingUuid(dokumentbestilling.getDokumentbestillingUuid())
                .medYtelseType(utledYtelseType(dokumentbestilling.getYtelseType().getKode()))
                .medFritekst(dokumentbestilling.getFritekst())
                .medHistorikkAktør(utledHistorikkAktør(dokumentbestilling.getHistorikkAktør()))
                .medDokumentMalType(utleddokumentMalType(dokumentbestilling.getDokumentMal()))
                .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(dokumentbestilling.getArsakskode()))
                .medBehandlendeEnhetNavn(dokumentbestilling.getBehandlendeEnhetNavn())
                .build();
    }

    public static DokumentHendelse mapDokumentHendelseFraV2Dto(DokumentbestillingV2Dto dokumentbestilling) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(dokumentbestilling.behandlingUuid())
                .medBestillingUuid(dokumentbestilling.dokumentbestillingUuid())
                .medYtelseType(utledYtelseType(dokumentbestilling.ytelseType()))
                .medFritekst(dokumentbestilling.fritekst())
                .medHistorikkAktør(utledHistorikkAktør(dokumentbestilling.historikkAktør().name()))
                .medDokumentMalType(utleddokumentMalType(dokumentbestilling.dokumentMal()))
                .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(dokumentbestilling.arsakskode()))
                .medBehandlendeEnhetNavn(dokumentbestilling.behandlendeEnhetNavn())
                .build();
    }
}

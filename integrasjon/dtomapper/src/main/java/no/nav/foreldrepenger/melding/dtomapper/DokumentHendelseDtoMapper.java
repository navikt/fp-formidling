package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
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
}

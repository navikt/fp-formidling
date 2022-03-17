package no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk;

import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkinnslagType;
import no.nav.historikk.JournalpostId;
import no.nav.historikk.kodeverk.HistorikkAktørEnum;
import no.nav.historikk.v1.HistorikkInnslagDokumentLink;
import no.nav.historikk.v1.HistorikkInnslagV1;

@Deprecated(forRemoval = true)
public class HistorikkTilDtoMapper {

    public static HistorikkInnslagV1 mapHistorikkinnslag(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return new HistorikkInnslagV1.Builder()
                .medBehandlingUuid(dokumentHistorikkinnslag.getBehandlingUuid())
                .medHistorikkUuid(dokumentHistorikkinnslag.getHistorikkUuuid())
                .medOpprettetTidspunkt(dokumentHistorikkinnslag.getOpprettetTidspunkt())
                .medHistorikkAktørType(HistorikkAktørEnum.VEDTAKSLØSNINGEN)
                .medType(HistorikkinnslagType.BREV_SENT.getKode())
                .medDokumentLinker(mapLink(dokumentHistorikkinnslag))
                .medAvsender("FP-FORMIDLING")
                .build();
    }

    private static List<HistorikkInnslagDokumentLink> mapLink(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return Collections.singletonList(new HistorikkInnslagDokumentLink.Builder()
                .medDokumentId(dokumentHistorikkinnslag.getDokumentId())
                .medJournalpostId(new JournalpostId(dokumentHistorikkinnslag.getJournalpostId().getVerdi()))
                .medLinkTekst(dokumentHistorikkinnslag.getDokumentMalType().getNavn())
                .build());
    }
}

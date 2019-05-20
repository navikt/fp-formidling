package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.historikk.JournalpostId;
import no.nav.historikk.kodeverk.HistorikkAktørEnum;
import no.nav.historikk.kodeverk.HistorikkInnslagFeltTypeEnum;
import no.nav.historikk.v1.HistorikkInnslagDel;
import no.nav.historikk.v1.HistorikkInnslagDokumentLink;
import no.nav.historikk.v1.HistorikkInnslagFelt;
import no.nav.historikk.v1.HistorikkInnslagV1;

public class HistorikkTilDtoMapper {

    private static Map<HistorikkAktør, HistorikkAktørEnum> historikkaktørMap = new HashMap<>();


    static {
        historikkaktørMap.put(HistorikkAktør.SAKSBEHANDLER, HistorikkAktørEnum.SAKSBEHANDLER);
        historikkaktørMap.put(HistorikkAktør.BESLUTTER, HistorikkAktørEnum.BESLUTTER);
        historikkaktørMap.put(HistorikkAktør.VEDTAKSLØSNINGEN, HistorikkAktørEnum.VEDTAKSLØSNINGEN);
        historikkaktørMap.put(HistorikkAktør.ARBEIDSGIVER, HistorikkAktørEnum.ARBEIDSGIVER);
        historikkaktørMap.put(HistorikkAktør.SØKER, HistorikkAktørEnum.SØKER);
    }

    public static HistorikkInnslagV1 mapHistorikkinnslag(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return new HistorikkInnslagV1.Builder()
                .medBehandlingUuid(dokumentHistorikkinnslag.getBehandlingUuid())
                .medAktør(mapHistorikkaktør(dokumentHistorikkinnslag))
                .medType(HistorikkinnslagType.BREV_SENT.getKode())
                .medDokumentLinker(mapLink(dokumentHistorikkinnslag))
                .medHistorikkInnslagDeler(mapDel(dokumentHistorikkinnslag))
                .build();
    }

    private static HistorikkAktørEnum mapHistorikkaktør(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return historikkaktørMap.get(dokumentHistorikkinnslag.getHistorikkAktør());
    }

    private static List<HistorikkInnslagDel> mapDel(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        HistorikkInnslagDel.Builder delBuilder = HistorikkInnslagDel.builder();
        Collections.singletonList(HistorikkInnslagFelt.builder()
                .medFeltType(HistorikkInnslagFeltTypeEnum.BEGRUNNELSE)
                .medTilVerdi(dokumentHistorikkinnslag.getDokumentMalType().getNavn())
                .build(delBuilder));
        return Collections.singletonList(delBuilder.build());
    }

    private static List<HistorikkInnslagDokumentLink> mapLink(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        return Collections.singletonList(new HistorikkInnslagDokumentLink.Builder()
                .medDokumentId(dokumentHistorikkinnslag.getDokumentId())
                .medJournalpostId(new JournalpostId(dokumentHistorikkinnslag.getJournalpostId().getVerdi()))
                .medLinkTekst(dokumentHistorikkinnslag.getDokumentMalType().getNavn())
                .build());
    }
}

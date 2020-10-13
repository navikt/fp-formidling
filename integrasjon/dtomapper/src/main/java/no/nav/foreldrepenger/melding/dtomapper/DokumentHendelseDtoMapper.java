package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class DokumentHendelseDtoMapper {

    public DokumentHendelseDtoMapper() {
        //CDI
    }

    private RevurderingVarslingÅrsak utledRevurderingVarslingsårsak(String varslingsårsak) {
        if (StringUtils.nullOrEmpty(varslingsårsak)) {
            return RevurderingVarslingÅrsak.UDEFINERT;
        }
        return RevurderingVarslingÅrsak.fraKode(varslingsårsak);
    }

    private FagsakYtelseType utledYtelseType(String ytelseType) {
        if (StringUtils.nullOrEmpty(ytelseType)) {
            return null;
        }
        return FagsakYtelseType.fraKode(ytelseType);
    }

    private DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (StringUtils.nullOrEmpty(dokumentmal)) {
            return null;
        }
        return DokumentMalType.fraKode(dokumentmal);
    }

    private HistorikkAktør utledHistorikkAktør(String historikkAktør) {
        if (StringUtils.nullOrEmpty(historikkAktør)) {
            return HistorikkAktør.UDEFINERT;
        }
        return HistorikkAktør.fraKode(historikkAktør);
    }

    public DokumentHendelse mapDokumentHendelseFraDtoForKafka(DokumentbestillingV1 dokumentbestilling) {
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

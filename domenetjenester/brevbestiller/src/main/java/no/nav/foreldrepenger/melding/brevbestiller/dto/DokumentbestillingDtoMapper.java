package no.nav.foreldrepenger.melding.brevbestiller.dto;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class DokumentbestillingDtoMapper {

    public DokumentbestillingDtoMapper() {
        //CDI
    }

    public DokumentHendelse mapDokumentbestillingFraDtoForEndepunkt(DokumentbestillingDto brevDto) {
        return DokumentHendelse.builder()
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
                .medErOpphevetKlage(brevDto.isErOpphevetKlage())
                .build();
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
}

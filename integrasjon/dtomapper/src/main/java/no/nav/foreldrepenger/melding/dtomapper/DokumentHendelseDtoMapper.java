package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.felles.dokumentbestilling.v1.DokumentbestillingV1;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class DokumentHendelseDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public DokumentHendelseDtoMapper() {
        //CDI
    }

    @Inject
    public DokumentHendelseDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
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
        return DokumentMalType.fraKode(dokumentmal);
    }

    private HistorikkAktør utledHistorikkAktør(String historikkAktør) {
        if (StringUtils.nullOrEmpty(historikkAktør)) {
            return HistorikkAktør.UDEFINERT;
        }
        return kodeverkRepository.finn(HistorikkAktør.class, historikkAktør);
    }

    public DokumentHendelse mapDokumentHendelseFraDtoForKafka(DokumentbestillingV1 dokumentbestilling) {
        return new DokumentHendelse.Builder()
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

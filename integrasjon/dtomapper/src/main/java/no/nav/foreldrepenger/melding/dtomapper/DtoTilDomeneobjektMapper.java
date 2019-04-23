package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class DtoTilDomeneobjektMapper {

    private KodeverkRepository kodeverkRepository;
    private DokumentRepository dokumentRepository;

    public DtoTilDomeneobjektMapper() {
        //CDI
    }

    @Inject
    public DtoTilDomeneobjektMapper(KodeverkRepository kodeverkRepository,
                                    DokumentRepository dokumentRepository) {
        this.kodeverkRepository = kodeverkRepository;
        this.dokumentRepository = dokumentRepository;
    }


    public DokumentHendelse fraDto(DokumentHendelseDto hendelseDto) {
        //TODO putt bare ett sted!
        //TODO ha alle feltene..
        //Ny modul under domenetjenester? :) Hva skal den hete..?
        return new DokumentHendelse.Builder()
                .medBehandlingId(hendelseDto.getBehandlingId())
                .medBehandlingType(utledBehandlingType(hendelseDto.getBehandlingType()))
                .medYtelseType(utledYtelseType(hendelseDto.getYtelseType()))
                .medFritekst(hendelseDto.getFritekst())
                .medTittel(hendelseDto.getTittel())
                .medHistorikkAktør(utledHistorikkAktør(hendelseDto.getHistorikkAktør()))
                .medDokumentMalType(utleddokumentMalType(hendelseDto.getDokumentMal()))
                .medGjelderVedtak(hendelseDto.isGjelderVedtak())
                .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(hendelseDto.getArsakskode()))
                .medErOpphevetKlage(hendelseDto.getErOpphevetKlage())
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

    private BehandlingType utledBehandlingType(String behandlingType) {
        if (StringUtils.nullOrEmpty(behandlingType)) {
            return BehandlingType.UDEFINERT;
        }
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
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

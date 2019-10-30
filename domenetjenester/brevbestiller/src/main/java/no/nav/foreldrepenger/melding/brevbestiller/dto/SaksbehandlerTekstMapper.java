package no.nav.foreldrepenger.melding.brevbestiller.dto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.kontrakter.formidling.v1.TekstFraSaksbehandlerDto;
import no.nav.foreldrepenger.melding.dokumentdata.SaksbehandlerTekst;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class SaksbehandlerTekstMapper {
    private KodeverkRepository kodeverkRepository;

    public SaksbehandlerTekstMapper() { //CDI
    }

    @Inject
    public SaksbehandlerTekstMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public SaksbehandlerTekst mapSaksbehandlerTekstFraDto(TekstFraSaksbehandlerDto tekstFraSaksbehandlerDto) {
        return SaksbehandlerTekst.builder()
                .medBehandlingUuid(tekstFraSaksbehandlerDto.getBehandlingUuid())
                .medVedtaksbrev(utledFormidlingVedtaksbrev(tekstFraSaksbehandlerDto.getVedtaksbrev()))
                .medAvklarFritekst(tekstFraSaksbehandlerDto.getAvklarFritekst())
                .medTittel(tekstFraSaksbehandlerDto.getTittel())
                .medFritekst(tekstFraSaksbehandlerDto.getFritekst())
                .build();
    }

    public TekstFraSaksbehandlerDto mapSaksbehandlerTekstTilDto(SaksbehandlerTekst saksbehandlerTekst) {
        TekstFraSaksbehandlerDto tekstFraSaksbehandlerDto = new TekstFraSaksbehandlerDto();
        tekstFraSaksbehandlerDto.setBehandlingUuid(saksbehandlerTekst.getBehandlingUuid());
        tekstFraSaksbehandlerDto.setVedtaksbrev(VedtaksbrevMapper.fraEntitet(saksbehandlerTekst.getVedtaksbrev()));
        tekstFraSaksbehandlerDto.setAvklarFritekst(saksbehandlerTekst.getAvklarFritekst());
        tekstFraSaksbehandlerDto.setTittel(saksbehandlerTekst.getTittel());
        tekstFraSaksbehandlerDto.setFritekst(saksbehandlerTekst.getFritekst());
        return tekstFraSaksbehandlerDto;
    }

    private Vedtaksbrev utledFormidlingVedtaksbrev(no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev vedtaksbrev) {
        if (vedtaksbrev == null || StringUtils.nullOrEmpty(vedtaksbrev.getKode())) {
            return Vedtaksbrev.UDEFINERT;
        }
        return kodeverkRepository.finn(Vedtaksbrev.class, vedtaksbrev.getKode());
    }
}

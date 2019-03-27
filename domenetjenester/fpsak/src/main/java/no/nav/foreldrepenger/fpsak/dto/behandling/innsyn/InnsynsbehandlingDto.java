package no.nav.foreldrepenger.fpsak.dto.behandling.innsyn;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.VedtaksdokumentasjonDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class InnsynsbehandlingDto {

    private LocalDate innsynMottattDato;
    private KodeDto innsynResultatType;
    private List<VedtaksdokumentasjonDto> vedtaksdokumentasjon  = new ArrayList<>();
    private List<InnsynDokumentDto> dokumenter = new ArrayList<>();

    public LocalDate getInnsynMottattDato() {
        return innsynMottattDato;
    }

    public KodeDto getInnsynResultatType() {
        return innsynResultatType;
    }

    public void setInnsynMottattDato(LocalDate innsynMottattDato) {
        this.innsynMottattDato = innsynMottattDato;
    }

    public void setInnsynResultatType(KodeDto innsynResultatType) {
        this.innsynResultatType = innsynResultatType;
    }

    public void setVedtaksdokumentasjon(List<VedtaksdokumentasjonDto> vedtaksdokumentasjon) {
        this.vedtaksdokumentasjon = vedtaksdokumentasjon;
    }

    public List<VedtaksdokumentasjonDto> getVedtaksdokumentasjon() {
        return vedtaksdokumentasjon;
    }

    public void setDokumenter(List<InnsynDokumentDto> dokumenter) {
        this.dokumenter = dokumenter;
    }

    public List<InnsynDokumentDto> getDokumenter() {
        return dokumenter;
    }
}

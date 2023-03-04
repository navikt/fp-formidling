package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    private MottattdokumentMapper() {
    }

    public static LocalDate finnSisteMottatteSøknad(List<MottattDokument> mottatteDokumenter) {
        return mottatteDokumenter.stream()
            .filter(dok -> DokumentKategori.SØKNAD.equals(dok.dokumentKategori()))
            .max(Comparator.comparing(MottattDokument::mottattDato))
            .map(MottattDokument::mottattDato)
            .orElse(LocalDate.now());
    }

    public static LocalDate finnførsteMottatteSøknad(List<MottattDokument> mottatteDokumenter) {
        return mottatteDokumenter.stream()
            .filter(dok -> DokumentKategori.SØKNAD.equals(dok.dokumentKategori()))
            .min(Comparator.comparing(MottattDokument::mottattDato))
            .map(MottattDokument::mottattDato)
            .orElse(LocalDate.now());
    }
}

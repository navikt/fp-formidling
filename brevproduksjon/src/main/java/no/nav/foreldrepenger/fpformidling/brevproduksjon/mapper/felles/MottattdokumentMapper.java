package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    public static LocalDate finnSøknadsdatoFraMottatteDokumenter(Behandling behandling, List<MottattDokument> mottatteDokumenter) {
        return mottatteDokumenter.stream()
                .filter(dok -> BehandlingMapper.gjelderEndringsøknad(behandling) ? velgEndringssøknad(dok) : velgSøknad(dok))
                .sorted(Comparator.comparing(MottattDokument::mottattDato).reversed())
                .findFirst()
                .map(MottattDokument::mottattDato).orElse(LocalDate.now());
    }

    private static boolean velgEndringssøknad(MottattDokument dok) {
        return dok.dokumentTypeId() != null && dok.dokumentTypeId().erEndringsøknadType();
    }

    private static boolean velgSøknad(MottattDokument dok) {
        return dok.dokumentTypeId() != null && (dok.dokumentTypeId().erSøknadType() || DokumentKategori.SØKNAD.equals(dok.dokumentKategori()));
    }
}
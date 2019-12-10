package no.nav.foreldrepenger.melding.datamapper.domene;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    public static XMLGregorianCalendar finnSøknadsDatoFraMottatteDokumenter(Behandling behandling, List<MottattDokument> mottatteDokumer) {
        return XmlUtil.finnDatoVerdiAvUtenTidSone(finnSøknadDokument(behandling, mottatteDokumer));
    }

    private static LocalDate finnSøknadDokument(Behandling behandling, List<MottattDokument> mottatteDokumer) {
        return mottatteDokumer.stream()
                .filter(dok -> BehandlingMapper.gjelderEndringsøknad(behandling) ? velgEndringssøknad(dok) : velgSøknad(dok))
                .sorted(Comparator.comparing(MottattDokument::getMottattDato).reversed())
                .findFirst()
                .map(MottattDokument::getMottattDato).orElse(LocalDate.now());
    }


    private static boolean velgEndringssøknad(MottattDokument dok) {
        return dok.getDokumentTypeId() != null && dok.getDokumentTypeId().erEndringsøknadType();
    }

    private static boolean velgSøknad(MottattDokument dok) {
        return dok.getDokumentTypeId() != null && dok.getDokumentTypeId().erSøknadType();
    }
}

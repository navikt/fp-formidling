package no.nav.foreldrepenger.melding.datamapper.domene;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    public static XMLGregorianCalendar finnSøknadsdatoFraMottatteDokumenterXml(Behandling behandling, List<MottattDokument> mottatteDokumenter) {
        return XmlUtil.finnDatoVerdiAvUtenTidSone(finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter));
    }

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

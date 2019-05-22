package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    public static XMLGregorianCalendar finnSøknadsDatoFraMottatteDokumenter(Behandling behandling, List<MottattDokument> mottatteDokumer) {
        return mottatteDokumer.stream()
                .filter(dok -> BehandlingMapper.gjelderEndringsøknad(behandling) ? velgEndringssøknad(dok) : velgSøknad(dok))
                .findFirst()
                .map(MottattDokument::getMottattDato)
                .map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .orElseThrow(IllegalStateException::new);
    }


    private static boolean velgEndringssøknad(MottattDokument dok) {
        return dok.getDokumentTypeId() != null && dok.getDokumentTypeId().erEndringsøknadType();
    }

    private static boolean velgSøknad(MottattDokument dok) {
        return dok.getDokumentTypeId() != null && dok.getDokumentTypeId().erSøknadType();
    }
}

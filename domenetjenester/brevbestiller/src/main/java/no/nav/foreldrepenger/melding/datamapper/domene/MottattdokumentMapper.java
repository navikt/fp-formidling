package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

public class MottattdokumentMapper {

    public static XMLGregorianCalendar finnSøknadsDatoFraMottatteDokumenter(List<MottattDokument> mottatteDokumer) {
        return mottatteDokumer.stream()
                .filter(dok -> dok.getDokumentTypeId() != null && dok.getDokumentTypeId().erSøknadType())
                .findFirst()
                .map(MottattDokument::getMottattDato)
                .map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .orElseThrow(IllegalStateException::new);
    }
}

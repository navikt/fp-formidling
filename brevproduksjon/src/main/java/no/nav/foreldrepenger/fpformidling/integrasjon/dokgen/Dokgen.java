package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;

public interface Dokgen {

    byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata);

}

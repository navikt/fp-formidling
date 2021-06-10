package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;

public interface Dokgen {

    byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata);

}

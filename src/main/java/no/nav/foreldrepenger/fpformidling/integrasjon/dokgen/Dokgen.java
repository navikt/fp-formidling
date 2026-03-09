package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;

/**
 * Brukes til generering av dokumenter.
 */
public interface Dokgen {

    byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata);

    String genererHtml(String maltype, Språkkode språkkode, Dokumentdata dokumentdata);
}

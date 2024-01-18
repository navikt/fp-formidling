package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;

/**
 * Brukes til generering av dokumenter.
 * Swagger dokumentasjon: <a href="https://fpdokgen.dev.intern.nav.no/swagger-ui/index.html">Swagger API dokumentasjon i dev.</a>
 */
public interface Dokgen {

    byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata);

}

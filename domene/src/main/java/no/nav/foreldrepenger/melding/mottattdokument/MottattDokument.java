package no.nav.foreldrepenger.melding.mottattdokument;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;

public record MottattDokument(LocalDate mottattDato, DokumentTypeId dokumentTypeId, DokumentKategori dokumentKategori) {
}

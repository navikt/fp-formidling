package no.nav.foreldrepenger.fpformidling.mottattdokument;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentTypeId;

public record MottattDokument(LocalDate mottattDato, DokumentTypeId dokumentTypeId, DokumentKategori dokumentKategori) {
}

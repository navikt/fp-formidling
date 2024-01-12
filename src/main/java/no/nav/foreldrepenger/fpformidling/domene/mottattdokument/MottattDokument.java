package no.nav.foreldrepenger.fpformidling.domene.mottattdokument;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentTypeId;

public record MottattDokument(LocalDate mottattDato, DokumentTypeId dokumentTypeId, DokumentKategori dokumentKategori) {
}

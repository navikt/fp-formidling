package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentKategori;

public record MottattDokumentDto(LocalDate mottattDato, String dokumentTypeId, DokumentKategori dokumentKategori) {

}

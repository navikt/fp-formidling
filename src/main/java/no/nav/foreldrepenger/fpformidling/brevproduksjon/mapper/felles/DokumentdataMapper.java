package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;

public interface DokumentdataMapper {

    String getTemplateNavn();

    Dokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelseEntitet hendelse, Behandling behandling, boolean erUtkast);
}

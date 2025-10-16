package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

public interface DokumentdataMapper {

    String getTemplateNavn();

    Dokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, BrevGrunnlagDto behandling, boolean erUtkast);
}

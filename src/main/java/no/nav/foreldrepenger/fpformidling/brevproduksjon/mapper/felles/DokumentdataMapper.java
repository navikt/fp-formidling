package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;

public interface DokumentdataMapper {

    String getTemplateNavn();

    Dokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, BrevGrunnlag behandling, boolean erUtkast);
}

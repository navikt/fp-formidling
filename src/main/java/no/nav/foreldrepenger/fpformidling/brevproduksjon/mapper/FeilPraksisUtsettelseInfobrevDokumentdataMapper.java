package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FeilPraksisUtsettelseInfobrevDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV)
public class FeilPraksisUtsettelseInfobrevDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-feil-praksis-utsettelse-infobrev";
    }

    @Override
    public FeilPraksisUtsettelseInfobrevDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling, boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(getBrevDato(dokumentFelles));
        return new FeilPraksisUtsettelseInfobrevDokumentdata(fellesBuilder.build());
    }

    private static String getBrevDato(DokumentFelles dokumentFelles) {
        return dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null;
    }
}

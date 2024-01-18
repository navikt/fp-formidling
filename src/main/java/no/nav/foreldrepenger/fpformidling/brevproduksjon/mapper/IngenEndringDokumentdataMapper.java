package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.IngenEndringDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

import jakarta.enterprise.context.ApplicationScoped;

import static no.nav.foreldrepenger.fpformidling.domene.typer.Dato.formaterDato;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INGEN_ENDRING)
public class IngenEndringDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "ingen-endring";
    }

    @Override
    public IngenEndringDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                       DokumentHendelse hendelse,
                                                       Behandling behandling,
                                                       boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        return IngenEndringDokumentdata.ny().medFelles(fellesBuilder.build()).build();
    }
}

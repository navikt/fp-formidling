package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FritekstbrevHtmlDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FRITEKSTBREV_HTML)
public class FritekstbrevHtmlDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "fritekstbrev-html";
    }

    @Override
    public FritekstbrevHtmlDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                           DokumentHendelse hendelse,
                                                           Behandling behandling,
                                                           boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        return FritekstbrevHtmlDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHtml(finnFritekstHtml(hendelse, behandling))
            .build();
    }

    private String finnFritekstHtml(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty() ? hendelse.getFritekst() : behandling.getBehandlingsresultat()
                .getFritekstbrev();
    }
}

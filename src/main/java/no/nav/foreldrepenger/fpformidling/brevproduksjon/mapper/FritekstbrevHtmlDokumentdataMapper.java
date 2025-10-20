package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FritekstbrevHtmlDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

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
                                                           BrevGrunnlagDto behandling,
                                                           boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);

        return FritekstbrevHtmlDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHtml(finnFritekstHtml(hendelse, behandling.behandlingsresultat()))
            .build();
    }

    private String finnFritekstHtml(DokumentHendelse hendelse, BrevGrunnlagDto.Behandlingsresultat behandlingsresultat) {
        return
            hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty() ? hendelse.getFritekst() : behandlingsresultat.fritekst().brødtekst();
    }
}

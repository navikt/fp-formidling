package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FritekstbrevDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FRITEKSTBREV)
public class FritekstbrevDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "fritekstbrev";
    }

    @Override
    public FritekstbrevDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                       DokumentHendelse hendelse,
                                                       BrevGrunnlagDto behandling,
                                                       boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);

        var behandlingsresultat = behandling.behandlingsresultat();
        return FritekstbrevDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medOverskrift(finnOverskrift(hendelse, behandlingsresultat))
            .medBrødtekst(FritekstDto.fra(finnBrødtekst(hendelse, behandlingsresultat)))
            .build();
    }

    private String finnOverskrift(DokumentHendelse hendelse, BrevGrunnlagDto.Behandlingsresultat behandlingsresultat) {
        return hendelse.getTittel() != null && !hendelse.getTittel().isEmpty() ? hendelse.getTittel() : behandlingsresultat.fritekst().overskrift();
    }

    private String finnBrødtekst(DokumentHendelse hendelse, BrevGrunnlagDto.Behandlingsresultat behandlingsresultat) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty() ? hendelse.getFritekst() : behandlingsresultat.fritekst().brødtekst();
    }
}

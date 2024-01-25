package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FritekstbrevDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
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
                                                       Behandling behandling,
                                                       boolean erUtkast) {
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        return FritekstbrevDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medOverskrift(finnOverskrift(hendelse, behandling))
            .medBrødtekst(FritekstDto.fra(finnBrødtekst(hendelse, behandling)))
            .build();
    }

    private String finnOverskrift(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getTittel() != null && !hendelse.getTittel().isEmpty() ? hendelse.getTittel() : behandling.getBehandlingsresultat()
            .getOverskrift();
    }

    private String finnBrødtekst(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty() ? hendelse.getFritekst() : behandling.getBehandlingsresultat()
            .getFritekstbrev();
    }
}

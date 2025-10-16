package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.INFO_OM_HENLEGGELSE)
public class HenleggeDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "henleggelse";
    }

    @Override
    public HenleggelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                      DokumentHendelse hendelse,
                                                      BrevGrunnlagDto behandling,
                                                      boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        return HenleggelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medVanligBehandling(behandling.erFørstegangssøknad() || behandling.erRevurdering())
            .medAnke(behandling.erAnke())
            .medInnsyn(behandling.erInnsyn())
            .medKlage(behandling.erKlage())
            .build();
    }

}

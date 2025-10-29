package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata.InnsynResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata.ny;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.INNSYN_SVAR)
public class InnsynDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;

    @Inject
    public InnsynDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "innsyn";
    }

    @Override
    public InnsynDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, BrevGrunnlagDto behandling, boolean erUtkast) {
        var innsynsBehandling = behandling.innsynBehandling();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);
        FritekstDto.fraFritekst(hendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        return ny()
            .medFelles(fellesBuilder.build())
            .medKlagefrist(brevParametere.getKlagefristUker())
            .medInnsynResultat(map(innsynsBehandling.innsynResultatType()))
            .build();
    }

    private InnsynResultatType map(BrevGrunnlagDto.InnsynBehandling.InnsynResultatType innsynResultatType) {
        return switch (innsynResultatType) {
            case INNVILGET -> InnsynResultatType.INNV;
            case DELVIS_INNVILGET -> InnsynResultatType.DELV;
            case AVVIST -> InnsynResultatType.AVVIST;
            case UDEFINERT -> throw new IllegalStateException("Unexpected value: " + innsynResultatType);
        };
    }
}

package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.domene.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerAnnullertDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_ANNULLERT)
public class ForeldrepengerAnnullertDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    ForeldrepengerAnnullertDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForeldrepengerAnnullertDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-annullert";
    }

    @Override
    public ForeldrepengerAnnullertDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                  DokumentHendelse hendelse,
                                                                  Behandling behandling,
                                                                  boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        var startdatoUtsatt = domeneobjektProvider.hentStartdatoUtsatt(behandling);
        var harSøktOmNyPeriode = startdatoUtsatt.nyStartdato() != null;

        var dokumentdataBuilder = ForeldrepengerAnnullertDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHarSøktOmNyPeriode(harSøktOmNyPeriode)
            .medKlagefristUker(brevParametere.getKlagefristUker());

        if (harSøktOmNyPeriode) {
            dokumentdataBuilder.medPlanlagtOppstartDato(formaterDato(startdatoUtsatt.nyStartdato(), behandling.getSpråkkode()));
            dokumentdataBuilder.medKanBehandlesDato(formaterDato(startdatoUtsatt.nyStartdato().minusWeeks(4), behandling.getSpråkkode()));
        }

        return dokumentdataBuilder.build();
    }
}

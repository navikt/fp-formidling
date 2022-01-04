package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerAnnullertDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpsak.dto.uttak.StartdatoUtsattDto;

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
    public ForeldrepengerAnnullertDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                                  Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        StartdatoUtsattDto startdatoUtsatt = domeneobjektProvider.hentStartdatoUtsatt(behandling);
        boolean harSøktOmNyPeriode = startdatoUtsatt.nyStartdato() != null;

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
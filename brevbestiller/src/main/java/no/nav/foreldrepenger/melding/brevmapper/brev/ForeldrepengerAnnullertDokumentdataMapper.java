package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.StartdatoUtsattDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.ForeldrepengerAnnullertDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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
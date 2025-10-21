package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerAnnullertDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_ANNULLERT)
public class ForeldrepengerAnnullertDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;

    @Inject
    public ForeldrepengerAnnullertDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-annullert";
    }

    @Override
    public ForeldrepengerAnnullertDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                  DokumentHendelse hendelse,
                                                                  BrevGrunnlagDto behandling,
                                                                  boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var startdatoUtsatt = behandling.foreldrepenger().nyStartDatoVedUtsattOppstart();

        var harSøktOmNyPeriode = startdatoUtsatt != null;

        var dokumentdataBuilder = ForeldrepengerAnnullertDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHarSøktOmNyPeriode(harSøktOmNyPeriode)
            .medKlagefristUker(brevParametere.getKlagefristUker());

        if (harSøktOmNyPeriode) {
            dokumentdataBuilder.medPlanlagtOppstartDato(formaterDato(startdatoUtsatt, språkkode));
            dokumentdataBuilder.medKanBehandlesDato(formaterDato(startdatoUtsatt.minusWeeks(4), språkkode));
        }
        return dokumentdataBuilder.build();
    }
}

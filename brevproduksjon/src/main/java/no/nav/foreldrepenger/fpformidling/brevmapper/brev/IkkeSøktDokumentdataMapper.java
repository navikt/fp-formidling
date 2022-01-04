package no.nav.foreldrepenger.fpformidling.brevmapper.brev;

import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.datamapper.domene.IAYMapper;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.IKKE_SØKT)
public class IkkeSøktDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    IkkeSøktDokumentdataMapper() {
        //CDI
    }

    @Inject
    public IkkeSøktDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "ikke-sokt";
    }

    @Override
    public IkkeSøktDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                   Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);

        InntektArbeidYtelse iay = domeneobjektProvider.hentInntektArbeidYtelse(behandling);
        Inntektsmelding inntektsmelding = IAYMapper.hentNyesteInntektsmelding(iay);

        var dokumentdataBuilder = IkkeSøktDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medArbeidsgiverNavn(inntektsmelding.arbeidsgiverNavn())
                .medMottattDato(formaterDatoNorsk(inntektsmelding.innsendingstidspunkt()));

        return dokumentdataBuilder.build();
    }
}

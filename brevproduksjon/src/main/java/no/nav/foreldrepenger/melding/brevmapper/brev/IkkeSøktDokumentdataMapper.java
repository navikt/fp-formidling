package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.IAYMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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

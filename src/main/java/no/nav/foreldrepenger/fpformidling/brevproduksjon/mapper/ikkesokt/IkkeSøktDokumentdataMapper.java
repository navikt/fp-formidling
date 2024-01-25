package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesokt;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.IKKE_SØKT)
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
    public IkkeSøktDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                   DokumentHendelse hendelse,
                                                   Behandling behandling,
                                                   boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);

        var iay = domeneobjektProvider.hentInntektsmeldinger(behandling);
        var inntektsmelding = InntektsmeldingMapper.hentNyesteInntektsmelding(iay);

        var dokumentdataBuilder = IkkeSøktDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medArbeidsgiverNavn(inntektsmelding.arbeidsgiverNavn())
            .medMottattDato(formaterDatoNorsk(inntektsmelding.innsendingstidspunkt()));

        return dokumentdataBuilder.build();
    }
}

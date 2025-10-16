package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesokt;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.util.Comparator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.IkkeSøktDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.IKKE_SØKT)
public class IkkeSøktDokumentdataMapper implements DokumentdataMapper {

    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public IkkeSøktDokumentdataMapper(ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "ikke-sokt";
    }

    @Override
    public IkkeSøktDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                   DokumentHendelse hendelse,
                                                   BrevGrunnlagDto behandling,
                                                   boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);

        var inntektsmeldinger = behandling.inntektsmeldinger();
        var inntektsmelding = inntektsmeldinger.stream()
            .max(Comparator.comparing(BrevGrunnlagDto.Inntektsmelding::innsendingstidspunkt))
            .orElseThrow(() -> new IllegalStateException("Finner ingen inntektsmelding"));

        var arbeidsgiverNavn = arbeidsgiverTjeneste.hentArbeidsgiverNavn(inntektsmelding.arbeidsgiverReferanse());
        var dokumentdataBuilder = IkkeSøktDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medArbeidsgiverNavn(arbeidsgiverNavn)
            .medMottattDato(formaterDatoNorsk(inntektsmelding.innsendingstidspunkt().toLocalDate()));

        return dokumentdataBuilder.build();
    }
}
